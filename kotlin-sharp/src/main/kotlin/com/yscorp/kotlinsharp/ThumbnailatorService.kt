package com.yscorp.kotlinsharp

import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.sqrt

@Service
class ThumbnailatorService {

    fun resizeImage(file: MultipartFile, targetFileSizeKb: Int): ImageProcessingResult2 {
        val originalSizeKb = file.size / 1024.0

        val originalImage = ImageIO.read(file.inputStream)
        val originalWidth = originalImage.width
        val originalHeight = originalImage.height

        val (newWidth, newHeight) = calculateNewDimensions(originalWidth, originalHeight, originalSizeKb, targetFileSizeKb)

        val outputStream = ByteArrayOutputStream()

        Thumbnails.of(originalImage)
            .size(newWidth, newHeight)
            .outputFormat("jpg")
            .toOutputStream(outputStream)

        val resizedImageBytes = outputStream.toByteArray()
        val resizedImageSizeKb = resizedImageBytes.size / 1024.0

        return ImageProcessingResult2(
            buffer = resizedImageBytes,
            width = newWidth,
            height = newHeight,
            size = resizedImageSizeKb.toLong(),
            originalSize = originalSizeKb.toLong(),
            originalWidth = originalWidth,
            originalHeight = originalHeight,
            s3FileName = null
        )
    }

    private fun calculateNewDimensions(currentWidth: Int, currentHeight: Int, currentFileSizeKb: Double, targetFileSizeKb: Int): Pair<Int, Int> {
        val scaleFactor = sqrt(targetFileSizeKb / currentFileSizeKb)
        val newWidth = (currentWidth * scaleFactor).toInt()
        val newHeight = (currentHeight * scaleFactor).toInt()
        return Pair(newWidth, newHeight)
    }

}