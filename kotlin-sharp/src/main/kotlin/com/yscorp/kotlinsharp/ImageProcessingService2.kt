package com.example.imagedemo

import ImageInfo
import ImageProcessingResult
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Paths
import javax.imageio.ImageIO

@Service
class ImageProcessingService2 {
    private val objectMapper = jacksonObjectMapper()

    fun getNodeScriptPath(): String {
        return Paths.get(System.getProperty("user.dir"), "../node-image/imageProcessor2.js").toAbsolutePath().toString()
    }

    fun processImageFromMultipart(file: MultipartFile, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult2 {
        val fileBytes = file.bytes
        return processImageStream(fileBytes, format, width, height, compress)
    }

    fun processImageFromUrl(imageUrl: String, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult2 {
        val url = URL(imageUrl)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        return processImageStream(fileBytes, format, width, height, compress)
    }

    private fun processImageStream(fileBytes: ByteArray, format: String?, width: Int?, height: Int?, compress: Boolean): ImageProcessingResult2 {
        val nodeScriptPath = getNodeScriptPath()

        val args = listOf(
            "node", nodeScriptPath,
            "process",
            format ?: "",
            width?.toString() ?: "",
            height?.toString() ?: "",
            compress.toString()
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = processBuilder.start()

        // 스트림을 통해 파일 전송
        process.outputStream.use { os ->
            os.write(fileBytes)
            os.flush()
        }

        // 결과 이미지 데이터를 수신
        val output = ByteArrayOutputStream().use { baos ->
            process.inputStream.copyTo(baos)
            baos.toByteArray()
        }

        // JSON 메타데이터를 수신
        val metaDataJson = String(output.takeLastWhile { it.toInt() != 0 }.toByteArray())
        val imageData = output.dropLastWhile { it.toInt() != 0 }.toByteArray()
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("Image processing failed: $errorOutput")
        }

        if (metaDataJson.isBlank()) {
            throw RuntimeException("No output from image processing script")
        }

        val metaData: ImageProcessingResult2 = objectMapper.readValue(metaDataJson)
        metaData.buffer = imageData
        return metaData
    }

    fun compressImageFromMultipart(file: MultipartFile, quality: Int): ImageProcessingResult2 {
        val fileBytes = file.bytes
        return compressImageStream(fileBytes, quality)
    }

    fun compressImageFromUrl(imageUrl: String, quality: Int): ImageProcessingResult2 {
        val url = URL(imageUrl)
        val image = ImageIO.read(url)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val fileBytes = baos.toByteArray()
        return compressImageStream(fileBytes, quality)
    }

    private fun compressImageStream(fileBytes: ByteArray, quality: Int): ImageProcessingResult2 {
        val nodeScriptPath = getNodeScriptPath()

        val args = listOf(
            "node", nodeScriptPath,
            "compress",
            "",
            "",
            "",
            "true",
            quality.toString()
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = processBuilder.start()

        // 스트림을 통해 파일 전송
        process.outputStream.use { os ->
            os.write(fileBytes)
            os.flush()
        }

        // 결과 이미지 데이터를 수신
        val output = ByteArrayOutputStream().use { baos ->
            process.inputStream.copyTo(baos)
            baos.toByteArray()
        }

        // JSON 메타데이터를 수신
        val metaDataJson = String(output.takeLastWhile { it.toInt() != 0 }.toByteArray())
        val imageData = output.dropLastWhile { it.toInt() != 0 }.toByteArray()
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("Image compression failed: $errorOutput")
        }

        if (metaDataJson.isBlank()) {
            throw RuntimeException("No output from image compression script")
        }

        val metaData: ImageProcessingResult2 = objectMapper.readValue(metaDataJson)
        metaData.buffer = imageData
        return metaData
    }

    fun getImageInfo(fileBytes: ByteArray): ImageInfo {
        val nodeScriptPath = getNodeScriptPath()

        val args = listOf(
            "node", nodeScriptPath,
            "info",
            "", "", ""  // 포맷, 가로, 세로 모두 빈 값으로 설정하여 정보만 가져옴
        )

        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = Paths.get(System.getProperty("user.dir"), "../node-image/node_modules").toAbsolutePath().toString()

        val process = processBuilder.start()

        // 스트림을 통해 파일 전송
        process.outputStream.use { os ->
            os.write(fileBytes)
            os.flush()
        }

        val output = ByteArrayOutputStream().use { baos ->
            process.inputStream.copyTo(baos)
            baos.toByteArray()
        }

        val metaDataJson = String(output.takeLastWhile { it.toInt() != 0 }.toByteArray())
        val imageData = output.dropLastWhile { it.toInt() != 0 }.toByteArray()
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("Image info retrieval failed: $errorOutput")
        }

        if (metaDataJson.isBlank()) {
            throw RuntimeException("No output from image info script")
        }

        val result: ImageInfo = objectMapper.readValue(metaDataJson)
        return result
    }

    fun saveProcessedFile(fileBytes: ByteArray, outputPath: String) {
        val file = File(outputPath)
        file.parentFile.mkdirs() // 디렉토리가 없으면 생성
        FileOutputStream(file).use { it.write(fileBytes) }
    }
}

data class ImageProcessingResult2(
    var buffer: ByteArray,
    val width: Int,
    val height: Int,
    val size: Long,
    val s3FileName: String? = null
)
