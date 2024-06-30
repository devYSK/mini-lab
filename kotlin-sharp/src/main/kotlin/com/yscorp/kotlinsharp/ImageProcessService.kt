package com.yscorp.kotlinsharp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.math.min

@Service
class ImageProcessService {
    private val objectMapper = jacksonObjectMapper()

    companion object {
        private const val NODE_SCRIPT_PATH = "js/imageProcessor.js"
        private const val COMPRESS = "compress"
        private const val PROCESS = "process"
    }

    suspend fun processImageFromUrl(
        imageUrl: String,
        format: String,
        width: Int?,
        height: Int?,
        compress: Boolean,
    ): ImageProcessingResult {
        val fileBytes = bytesFromUrl(imageUrl)

        return processImageStream(fileBytes, format, width, height, compress)
    }

    suspend fun processImageFromMultipart(
        file: MultipartFile,
        format: String,
        width: Int?,
        height: Int?,
        compress: Boolean,
    ): ImageProcessingResult {
        val fileBytes = file.bytes
        return processImageStream(fileBytes, format, width, height, compress)
    }

    suspend fun compressImageFromUrl(imageUrl: String, quality: Int): ImageProcessingResult {
        val fileBytes = bytesFromUrl(imageUrl)

        val format = getImageFormat(fileBytes)
        return compressImageStream(fileBytes, format, quality)
    }

    suspend fun compressImageFromMultipart(file: MultipartFile, quality: Int): ImageProcessingResult {
        val fileBytes = file.bytes

        val format = file.originalFilename?.substringAfterLast('.') ?: "jpg"
// 원본 이미지 크기 가져오기
        val originalSize = getImageSize(fileBytes)
        val (originalWidth, originalHeight) = originalSize

        // targetWidth와 targetHeight 설정
        val targetWidth = 800 // 원하는 너비로 설정
        val targetHeight = 600 // 원하는 높이로 설정
//        return compressImageStream(fileBytes, format, quality)
        // 비율을 유지하면서 크기 조정
        val resizedBytes = resizeImage(fileBytes, format, originalWidth, originalHeight, targetWidth, targetHeight)
        return compressImageStream(resizedBytes, format, quality)
    }

    private fun getImageSize(fileBytes: ByteArray): Pair<Int, Int> {
        val inputStream = ByteArrayInputStream(fileBytes)
        val image: BufferedImage = ImageIO.read(inputStream)
        return Pair(image.width, image.height)
    }

    private suspend fun compressImageStream(fileBytes: ByteArray, format:String, quality: Int): ImageProcessingResult {
        val nodeScriptPath = getNodeScriptPath()

        val args = listOf(
            "node",
            nodeScriptPath,
            COMPRESS,
            format,
            "",
            "",
            "true",
            quality.toString()
        )

        return processingResult(args, fileBytes)
    }

    fun saveProcessedFile(fileBytes: ByteArray, outputPath: String) {
        val file = File(outputPath)
        file.parentFile.mkdirs() // 디렉토리가 없으면 생성

        FileOutputStream(file).use { it.write(fileBytes) }
    }

    private suspend fun processImageStream(
        fileBytes: ByteArray,
        format: String,
        width: Int?,
        height: Int?,
        compress: Boolean,
    ): ImageProcessingResult {
        val nodeScriptPath = getNodeScriptPath()

        println("nodeScriptPath: $nodeScriptPath")
        val args = listOf(
            "node", nodeScriptPath,
            PROCESS,
            format,
            width?.toString() ?: "",
            height?.toString() ?: "",
            compress.toString()
        )

        return processingResult(args, fileBytes)
    }

    private fun getNodePath(): String {
        val userHome = System.getProperty("user.home")

        return Paths.get(userHome, "node-image", "node_modules").toAbsolutePath().toString()
    }

    private fun getNodeScriptPath(): String {
        val userHome = System.getProperty("user.home")

        return Paths.get(userHome, "node-image", "imageProcessor.js").toAbsolutePath().toString()
    }

    suspend fun bytesFromUrl(url: String): ByteArray {
        val uri = URI(url)

        val image: BufferedImage = withContext(Dispatchers.IO) {
            ImageIO.read(uri.toURL())
        }

        val baos = ByteArrayOutputStream()

        withContext(Dispatchers.IO) {
            ImageIO.write(image, "jpg", baos)
        }

        return baos.toByteArray()
    }

    private suspend fun processingResult(
        args: List<String>,
        fileBytes: ByteArray,
    ): ImageProcessingResult = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder(args)
        processBuilder.environment()["NODE_PATH"] = getNodePath()

        val process = processBuilder.start()

        // 스트림을 통해 파일 전송
        try {
            process.outputStream.use { os ->
                os.write(fileBytes)
                os.flush()
            }
        } catch (e: Exception) {
            val errorOutput = ByteArrayOutputStream().use { baos ->
                process.errorStream.copyTo(baos)
                baos.toString()
            }
            println(errorOutput)
            println(e.message)
            throw e
        }

        // 결과 이미지 데이터를 수신
        val output = ByteArrayOutputStream().use { baos ->
            process.inputStream.copyTo(baos)
            baos.toByteArray()
        }
        val errorOutput = ByteArrayOutputStream().use { baos ->
            process.errorStream.copyTo(baos)
            baos.toString()
        }

        // JSON 메타데이터를 수신
        val metaDataJson = String(output.takeLastWhile { it.toInt() != 0 }.toByteArray())
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("Image compression failed: $errorOutput")
        }

        if (metaDataJson.isBlank()) {
            println("No output from image compression script " + errorOutput + "\n" + output)
            throw RuntimeException("No output from image compression script " + errorOutput)
        }

        val jsonResponse = objectMapper.readTree(metaDataJson)

        val imageDataArray = jsonResponse["buffer"]["data"].map { it.asInt().toByte() }.toByteArray()
        val metaData = ImageProcessingResult(
            width = jsonResponse["width"].asInt(),
            height = jsonResponse["height"].asInt(),
            size = jsonResponse["size"].asLong(),
            buffer = imageDataArray
        )

        return@withContext metaData
    }

    private fun getImageFormat(fileBytes: ByteArray): String {
        val inputStream = ByteArrayInputStream(fileBytes)
        val image = ImageIO.read(inputStream)
        val formatName = ImageIO.getImageReaders(image).next().formatName
        return formatName
    }

    private fun resizeImage(fileBytes: ByteArray, format: String, originalWidth: Int, originalHeight: Int, targetWidth: Int, targetHeight: Int): ByteArray {
        // 비율 유지하여 새로운 크기 계산
        val scaleFactor = min(targetWidth.toDouble() / originalWidth, targetHeight.toDouble() / originalHeight)

        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(ImageIO.read(ByteArrayInputStream(fileBytes)), 0, 0, newWidth, newHeight, null)
        graphics2D.dispose()

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(resizedImage, format, outputStream)
        return outputStream.toByteArray()
    }
}

data class ImageProcessingResult(
    var buffer: ByteArray,
    val width: Int,
    val height: Int,
    val size: Long,
    val s3FileName: String? = null,
) {

}
