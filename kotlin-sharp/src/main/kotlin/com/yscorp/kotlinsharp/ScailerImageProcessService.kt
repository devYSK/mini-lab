package com.yscorp.kotlinsharp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.imgscalr.Scalr
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.*
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.sqrt

@Service
class ScailerImageProcessService {

    suspend fun processImagesFromUrls2(url: String): ImageProcessingResult1 {
        withContext(Dispatchers.IO) {
            URI(url).toURL().openStream()
        }.use { inputStream ->
            return handleImage2(inputStream.readBytes(), url.substringAfterLast("/"))
        }
    }

    suspend fun processImagesFromMultipartFile(file: MultipartFile): ImageProcessingResult1 {
        return handleImage2(file.inputStream.readBytes(), file.originalFilename ?: "unknown")
    }

    private fun handleImage2(imageBytes: ByteArray, originalFilename: String, maxSizeBytes: Int = 200 * 1024): ImageProcessingResult1 {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes))

        val currentSizeBytes = imageBytes.size
        val originalWidth = image.width
        val originalHeight = image.height

        if (currentSizeBytes <= maxSizeBytes) {
            return ImageProcessingResult1(
                createCustomMultipartFile(imageBytes, originalFilename),
                originalWidth,
                originalHeight,
                currentSizeBytes.toLong(),
                currentSizeBytes.toLong(),
                originalWidth,
                originalHeight,
                originalFilename
            )
        }

        val (resizedImage, width, height) = resize(image, 1024)

        val outputImageBytes = ByteArrayOutputStream().use { baos ->
            ImageIO.write(resizedImage, getFileExtension(originalFilename), baos)
            baos.toByteArray()
        }

        return ImageProcessingResult1(
            createCustomMultipartFile(outputImageBytes, originalFilename),
            width,
            height,
            outputImageBytes.size.toLong(),
            currentSizeBytes.toLong(),
            originalWidth,
            originalHeight,
            originalFilename
        )
    }

    suspend fun processImagesFromMultipartFiles(file: MultipartFile): ImageProcessingResult2 {
        return handleImage(file.inputStream.readBytes(), file.originalFilename ?: "unknown")
    }

    suspend fun processImagesFromUrls(url: String): ImageProcessingResult2 {
        withContext(Dispatchers.IO) {
            URI(url).toURL().openStream()
        }.use { inputStream ->
            return handleImage(inputStream.readBytes(), url.substringAfterLast("/"))
        }
    }

    private fun handleImage(imageBytes: ByteArray, originalFilename: String, maxSizeBytes: Int = 200 * 1024): ImageProcessingResult2 {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes))

        val currentSizeBytes = imageBytes.size
        val originalWidth = image.width
        val originalHeight = image.height

        if (currentSizeBytes <= maxSizeBytes) {
            return ImageProcessingResult2(
                imageBytes,
                originalWidth,
                originalHeight,
                currentSizeBytes.toLong(),
                currentSizeBytes.toLong(),
                originalWidth,
                originalHeight,
                originalFilename
            )
        }

        val (resizedImage, width, height) = resize(image, 1024)
//        val (resizedImage, width, height) = resize(image, currentSizeBytes)

        val outputImageBytes = ByteArrayOutputStream().use { baos ->
            ImageIO.write(resizedImage, getFileExtension(originalFilename), baos)
            baos.toByteArray()
        }

        return ImageProcessingResult2(
            outputImageBytes,
            width,
            height,
            outputImageBytes.size.toLong(),
            currentSizeBytes.toLong(),
            originalWidth,
            originalHeight,
            originalFilename
        )
    }

    fun resize(image: BufferedImage, ratio: Int): Triple<BufferedImage, Int, Int> {
        val (width, height) = getResizeWithHeight(image.width, image.height, ratio)

        return Triple(Scalr.resize(image, width, height), width, height)
    }

    fun resize(image: BufferedImage, currentSizeBytes: Long): Triple<BufferedImage, Int, Int> {
        val (width, height) = calculateNewDimensions(image.width, image.height,
            ByteUtils.bytesToKilobytes(currentSizeBytes))

        return Triple(Scalr.resize(image, width, height), width, height)
    }

    fun calculateNewDimensions(currentWidth: Int, currentHeight: Int,
                               currentFileSizeKb: Double,
                               targetFileSizeKb: Int = 200
    ): Pair<Int, Int> {
        val scaleFactor = sqrt(targetFileSizeKb / currentFileSizeKb)
        val newWidth = (currentWidth * scaleFactor).toInt()
        val newHeight = (currentHeight * scaleFactor).toInt()
        return Pair(newWidth, newHeight)
    }

    private fun getResizeWithHeight(width: Int, height: Int, ratio: Int): Pair<Int, Int> {
        val ratioWidth = ratio.toDouble() / width
        val ratioHeight = ratio.toDouble() / height
        val calcRatio = min(1.0, min(ratioWidth, ratioHeight))
        val calcWidth = (width.toDouble() * calcRatio).toInt()
        val calcHeight = (height.toDouble() * calcRatio).toInt()
        return Pair(calcWidth, calcHeight)
    }

    private fun getFileExtension(filename: String?): String {
        return filename?.substringAfterLast('.', "png") ?: "png"
    }


    private fun createCustomMultipartFile(bytes: ByteArray, filename: String): MultipartFile {
        return CustomMultipartFile(bytes, "file", filename, "image/${getFileExtension(filename)}")
    }

}


data class ImageProcessingResult1(
    var file: MultipartFile,
    val width: Int,
    val height: Int,
    val size: Long,
    val originalSize: Long,
    val originalWidth: Int,
    val originalHeight: Int,
    val s3FileName: String? = null,
)

class CustomMultipartFile(
    private val input: ByteArray,
    private val names: String? = null,
    private val originalFilename: String? = null,
    private val contentType: String? = null
) : MultipartFile {

    //previous methods
    override fun isEmpty(): Boolean {
        return input.size === 0
    }

    override fun getSize(): Long {
        return input.size.toLong()
    }

    @Throws(IOException::class)
    override fun getBytes(): ByteArray {
        return input
    }

    override fun transferTo(dest: File) {
        FileOutputStream(dest).use { fos -> fos.write(input) }
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(input)
    }

    override fun getName(): String {
        return this.names!!
    }

    override fun getOriginalFilename(): String? {
        return this.originalFilename
    }

    override fun getContentType(): String? {
        return this.contentType
    }

}