package com.yscorp.kotlinsharp

import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.math.sqrt

@RestController
class ProcessController(
    private val imageProcessService: ImageProcessService,
) {


    @PostMapping("/upload")
    suspend fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        val originalSize = file.size
        val fileName = file.originalFilename

        println("originalSize: ${getFileSizeInKB(file)}")

        if (isOverSize(originalSize)) {
            val quality = calculateQuality(originalSize)

            val compressImageFromMultipart = imageProcessService.compressImageFromMultipart(file, quality)
            println("compressedSize: ${getFileSizeInKB(compressImageFromMultipart.buffer)}")
            val outputPath = "/Users/ysk/test/" + fileName
            println("outputPath: $outputPath")

            println(compressImageFromMultipart.height)
            println(compressImageFromMultipart.width)
            println(compressImageFromMultipart.s3FileName)

            imageProcessService.saveProcessedFile(compressImageFromMultipart.buffer, outputPath)
        } else {
            imageProcessService.saveProcessedFile(file.bytes, fileName!!)
        }

        return ResponseEntity.ok().build()
    }


    @PostMapping("/upload2")
    suspend fun uploadImage2(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        val originalSize = file.size
        val fileName = file.originalFilename

        println("originalSize: ${getFileSizeInKB(file)}")

        if (isOverSize(originalSize)) {
            val quality = calculateQuality(originalSize)

            val compressImageFromMultipart = imageProcessService.processImageFromMultipart(file, "jpg", 1000, 1000, false)
            println("compressedSize: ${getFileSizeInKB(compressImageFromMultipart.buffer)}")
            val outputPath = "/Users/ysk/test/" + fileName
            println("outputPath: $outputPath")

            println(compressImageFromMultipart.height)
            println(compressImageFromMultipart.width)
            println(compressImageFromMultipart.s3FileName)

            imageProcessService.saveProcessedFile(compressImageFromMultipart.buffer, outputPath)
        } else {
            imageProcessService.saveProcessedFile(file.bytes, fileName!!)
        }

        return ResponseEntity.ok().build()
    }

    @PostMapping("/upload-url")
    fun uploadImage(@RequestParam("file") url: String): ResponseEntity<Any> = runBlocking {

        val bytesFromUrl = imageProcessService.bytesFromUrl(url)

        val fileSizeInKB = getFileSizeInKB(bytesFromUrl)

        println("originalSize: $fileSizeInKB")

        val originalSize = bytesFromUrl.size.toLong()

        if (isOverSize(originalSize)) {
            val quality = calculateQuality(originalSize)
            val compressImageFromMultipart = imageProcessService.compressImageFromUrl(url, quality)
            println("compressedSize: ${getFileSizeInKB(compressImageFromMultipart.buffer)}")

            val fileName = "compressed" + url.substringAfterLast("/")
            val outputPath = "/Users/ysk/test/" + fileName
            println("outputPath: $outputPath")
            imageProcessService.saveProcessedFile(compressImageFromMultipart.buffer, outputPath)
        }

        return@runBlocking ResponseEntity.ok().build()
    }

    @PostMapping("/upload-url2")
    fun uploadImage2(@RequestParam("file") url: String): ResponseEntity<Any> = runBlocking {

        val bytesFromUrl = imageProcessService.bytesFromUrl(url)

        val fileSizeInKB = getFileSizeInKB(bytesFromUrl)

        println("originalSize: $fileSizeInKB")

        val originalSize = bytesFromUrl.size.toLong()

        if (isOverSize(originalSize)) {
            val image = ImageIO.read(ByteArrayInputStream(bytesFromUrl))

            val currentSizeBytes = bytesFromUrl.size
            val originalWidth = image.width
            val originalHeight = image.height

            val (width, height) = calculateNewDimensions(originalWidth, originalHeight, currentSizeBytes, 200 * 1024)

            val compressImageFromMultipart = imageProcessService.processImageFromUrl(url, "jpeg", width, height, false)
            println("compressedSize: ${getFileSizeInKB(compressImageFromMultipart.buffer)}")

            val fileName = "compressed" + url.substringAfterLast("/")
            val outputPath = "/Users/ysk/test/" + fileName
            println("outputPath: $outputPath")
            imageProcessService.saveProcessedFile(compressImageFromMultipart.buffer, outputPath)
        }

        return@runBlocking ResponseEntity.ok().build()
    }

    private fun calculateNewDimensions(currentWidth: Int, currentHeight: Int, currentSizeBytes: Int, maxSizeBytes: Int): Pair<Int, Int> {
        val scaleFactor = sqrt((maxSizeBytes / currentSizeBytes.toDouble()) / 2)
        val newWidth = (currentWidth * scaleFactor).toInt()
        val newHeight = (currentHeight * scaleFactor).toInt()

        return Pair(newWidth, newHeight)
    }

    private fun isOverSize(size: Long) = size > 200 * 1024

    fun calculateQuality(fileSize: Long): Int {
        val maxFileSize = 2 * 1024 * 1024 // 2MB
        val targetFileSize = 200 * 1024 // 200KB
        val minQuality = 10
        val maxQuality = 100

        return if (fileSize <= targetFileSize) {
            maxQuality
        } else {
            // 목표 크기를 달성하기 위해 품질 값을 계산합니다.
            val ratio = targetFileSize.toDouble() / fileSize
            val quality = (ratio * (maxQuality - minQuality) + minQuality).toInt()
            quality.coerceIn(minQuality, maxQuality)
        }
    }

    fun getFileSizeInKB(file: MultipartFile): Double {
        return file.size / 1024.0
    }

    fun getFileSizeInKB(byteArray: ByteArray): Double {
        return byteArray.size / 1024.0
    }

}