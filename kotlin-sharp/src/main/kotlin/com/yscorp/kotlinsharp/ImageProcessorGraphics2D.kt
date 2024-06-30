package com.yscorp.kotlinsharp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.*
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.sqrt


@Service
class ImageProcessorGraphics2D {

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

        val (newWidth, newHeight) = calculateNewDimensions(originalWidth, originalHeight, currentSizeBytes, maxSizeBytes)
        val resizedImage = resizeImage(image, newWidth, newHeight)

        val outputImageBytes = ByteArrayOutputStream().use { baos ->
            ImageIO.write(resizedImage, getFileExtension(originalFilename), baos)
            baos.toByteArray()
        }

        return ImageProcessingResult2(
            outputImageBytes,
            newWidth,
            newHeight,
            outputImageBytes.size.toLong(),
            currentSizeBytes.toLong(),
            originalWidth,
            originalHeight,
            originalFilename
        )
    }

    private fun calculateNewDimensions(currentWidth: Int, currentHeight: Int, currentSizeBytes: Int, maxSizeBytes: Int): Pair<Int, Int> {
        val scaleFactor = sqrt((maxSizeBytes / currentSizeBytes.toDouble()) / 2)
        val newWidth = (currentWidth * scaleFactor).toInt()
        val newHeight = (currentHeight * scaleFactor).toInt()

        return Pair(newWidth, newHeight)
    }

    private fun resizeImage(originalImage: BufferedImage, width: Int, height: Int): BufferedImage {
        val resizedImage = BufferedImage(width, height, originalImage.type)
        val g2d: Graphics2D = resizedImage.createGraphics()
        g2d.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        g2d.dispose()

        return resizedImage
    }

    private fun getFileExtension(filename: String?): String {
        return filename?.substringAfterLast('.', "png") ?: "png"
    }


}

data class ImageProcessingResult2(
    var buffer: ByteArray,
    val width: Int,
    val height: Int,
    val size: Long,
    val originalSize: Long,
    val originalWidth: Int,
    val originalHeight: Int,
    val s3FileName: String? = null,
)
