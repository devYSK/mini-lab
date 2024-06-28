import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.function.ServerResponse.async
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageProcessingService {
    private val objectMapper = jacksonObjectMapper()

    suspend fun getNodeScriptPath(): String {
        // Node.js 스크립트 경로를 새로운 디렉토리에서 가져오기
        return Paths.get(System.getProperty("user.dir"), "../node-image/imageProcessor.js").toAbsolutePath().toString()
    }

    suspend fun processImageFromMultipart(file: MultipartFile, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult {
        val fileBytes = file.bytes
        return processImage(fileBytes, format, width, height, compress)
    }

    suspend fun processImageFromUrl(imageUrl: String, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult {
        val url = URL(imageUrl)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        return processImage(fileBytes, format, width, height, compress)
    }

    suspend fun imageInfo(imageUrl: String): ImageInfo {
        val url = URL(imageUrl)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        return getImageInfo(fileBytes)
    }

    private suspend fun processImage(fileBytes: ByteArray, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult {
        val inputBase64 = Base64.getEncoder().encodeToString(fileBytes)
        val nodeScriptPath = getNodeScriptPath()

        val args = mutableListOf(
            "node", nodeScriptPath,
            "process",
            inputBase64,
            format ?: "",
            width?.toString() ?: "",
            height?.toString() ?: "",
            compress.toString()
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = withContext(Dispatchers.IO) {
            processBuilder.start()
        }
        val exitCode = withContext(Dispatchers.IO) {
            process.waitFor()
        }

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }

        if (exitCode != 0) {
            throw RuntimeException("Image processing failed: $errorOutput")
        }

        if (output.isBlank()) {
            throw RuntimeException("No output from image processing script")
        }

        println("Process output: $output")
        println("Process error output: $errorOutput")

        val result: ImageProcessingResult = objectMapper.readValue(output)
        return result
    }

    suspend fun compressImageFromMultipart(file: MultipartFile, quality: Int): ImageProcessingResult {
        val fileBytes = file.bytes
        return compressImage(fileBytes, quality)
    }

    suspend fun compressImageFromUrl(imageUrl: String, quality: Int): ImageProcessingResult {
        val url = URL(imageUrl)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        return compressImageStream(fileBytes, quality)
    }

    private suspend fun compressImage(fileBytes: ByteArray, quality: Int): ImageProcessingResult {
        val inputBase64 = Base64.getEncoder().encodeToString(fileBytes)
        val nodeScriptPath = getNodeScriptPath()

        val args = mutableListOf(
            "node", nodeScriptPath,
            "compress",
            inputBase64,
            "",
            "",
            "",
            "true",
            quality.toString()
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = processBuilder.start()
        val exitCode = process.waitFor()

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }

        if (exitCode != 0) {
            throw RuntimeException("Image compression failed: $errorOutput")
        }

        if (output.isBlank()) {
            throw RuntimeException("No output from image compression script")
        }

        val result: ImageProcessingResult = objectMapper.readValue(output)
        return result
    }

    private suspend fun compressImageStream(fileBytes: ByteArray, quality: Int): ImageProcessingResult {
        val nodeScriptPath = getNodeScriptPath()

        val args = listOf(
            "node", nodeScriptPath,
            "compress",
            Base64.getEncoder().encodeToString(fileBytes),
            "",
            "",
            "",
            "true",
            quality.toString()
        )


        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = withContext(Dispatchers.IO) {
            processBuilder.start()
        }

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }
        val exitCode = withContext(Dispatchers.IO) {
            process.waitFor()
        }

        if (exitCode != 0) {
            throw RuntimeException("Image compression failed: $errorOutput")
        }

        if (output.isBlank()) {
            throw RuntimeException("No output from image compression script")
        }

        val result: ImageProcessingResult = objectMapper.readValue(output)
        return result
    }

    suspend fun getImageInfo(fileBytes: ByteArray): ImageInfo {
        val inputBase64 = Base64.getEncoder().encodeToString(fileBytes)
        val nodeScriptPath = getNodeScriptPath()

        val args = mutableListOf(
            "node", nodeScriptPath,
            "info",
            inputBase64
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = processBuilder.start()
        val exitCode = process.waitFor()

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }

        if (exitCode != 0) {
            throw RuntimeException("Image info retrieval failed: $errorOutput")
        }

        if (output.isBlank()) {
            throw RuntimeException("No output from image info script")
        }

        println("Process output: $output")
        println("Process error output: $errorOutput")

        val result: ImageInfo = objectMapper.readValue(output)
        return result
    }

    suspend fun saveProcessedFile(fileBytes: ByteArray, outputPath: String) {
        val file = File(outputPath)
        file.parentFile.mkdirs() // 디렉토리가 없으면 생성
        FileOutputStream(file).use { it.write(fileBytes) }
    }

    suspend fun saveProcessedFile(encodedString: String, outputPath: String) {
        val file = File(outputPath)
        file.parentFile.mkdirs() // 디렉토리가 없으면 생성
        FileOutputStream(file).use { it.write(Base64.getDecoder().decode(encodedString)) }
    }

    suspend fun saveFromUrl(url: String ,outputPath: String) {
        val url = URL(url)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        saveProcessedFile(fileBytes, outputPath)
    }

}

data class ImageProcessingResult(
    var buffer: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val s3FileName: String? = null
)

data class ImageInfo(
    val width: Int,
    val height: Int,
    val size: Long
)
