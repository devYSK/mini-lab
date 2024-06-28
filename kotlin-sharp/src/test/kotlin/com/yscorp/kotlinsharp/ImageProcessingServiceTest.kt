package com.example.imagedemo

import ImageProcessingService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.*
import kotlin.reflect.jvm.reflect
import kotlin.system.measureTimeMillis

class ImageProcessingServiceTest {
    private val imageProcessingService = ImageProcessingService()

    private val log = LoggerFactory.getLogger(javaClass)


    suspend fun <T> runWithTimingLogging(block: suspend () -> T): T {
        val time = measureTimeMillis {
            block()
        }
        log.info("Task completed in $time ms, ${block.reflect()?.name}")
        return block()
    }

    @Test
    fun imageInfo () = runBlocking {

        mutableListOf("https://cdn.imweb.me/thumbnail/20221108/6be5b00277948.jpg",
            "https://www.cosinkorea.com/data/photos/20221146/art_16684142976506_5f5596.jpg",
            "https://www.jangup.com/news/photo/202207/87459_58853_4823.jpg",
            "https://www.shutterstock.com/shutterstock/photos/2414690613/display_1500/stock-photo-mythical-d-image-of-pink-dragon-2414690613.jpg",
            "https://www.shutterstock.com/shutterstock/photos/2460500699/display_1500/stock-photo-outdoor-photo-of-couple-traveling-under-blue-sky-2460500699.jpg"
            )
            .asFlow()
            .map { imageUrl ->
                async {
                    runWithTimingLogging {
                        val compressImageFromUrl = imageProcessingService.compressImageFromUrl(imageUrl, 50)
                        println(
                            "압축 후 : ${
                                ByteUtils.bytesToKilobytes(
                                    compressImageFromUrl.size
                                )
                            } "
                        )
                        imageProcessingService.saveProcessedFile(
                            compressImageFromUrl.buffer,
                            "/Users/ysk/test/" + imageUrl.substringAfterLast("/")
                        )
                    }
                }
            }
            .collect {
                it.await()
            }


//
//        val imageUrl = "https://i.pinimg.com/originals/02/25/07/0225070cf8ec553ded8a37e5cba74a23.jpg"
//        val result = imageProcessingService.imageInfo(imageUrl)
//
////        println(result)
//
//        println(ByteUtils.bytesToKilobytes(result.size))
//
//
//        val compressImageFromUrl = imageProcessingService.compressImageFromUrl(imageUrl, 10)
//
////        println(compressImageFromUrl)
//        println(ByteUtils.bytesToKilobytes(compressImageFromUrl.size))
//
//        imageProcessingService.saveProcessedFile(compressImageFromUrl.buffer, "/Users/ysk/test/compressed_image4.jpg")
//        imageProcessingService.saveFromUrl(imageUrl, "/Users/ysk/test/compressed_image5.jpg")
    }

    @Test
    fun `test processImageFromUrl`() = runBlocking {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://i.pinimg.com/originals/02/25/07/0225070cf8ec553ded8a37e5cba74a23.jpg"

        // URL에서 이미지를 처리하는 메서드를 호출합니다.
        val result = imageProcessingService.processImageFromUrl(imageUrl, "jpg", 100, 100, true)

        // 예상 결과 값을 설정합니다.
        val expectedWidth = 100
        val expectedHeight = 100

        // 결과 값을 검증합니다.
        assertEquals(expectedWidth, result.width)
        assertEquals(expectedHeight, result.height)
        assertTrue(result.size > 0)

        // 처리된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/processed_image3.jpg"
        val processedFileBytes = Base64.getDecoder().decode(result.buffer)
        imageProcessingService.saveProcessedFile(processedFileBytes, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

        // 테스트 후 파일을 삭제합니다.
//        Files.deleteIfExists(file.toPath())
    }

    @Test
    fun `test compressImageFromUrl`() = runBlocking  {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://via.placeholder.com/150"

        // URL에서 이미지를 압축하는 메서드를 호출합니다.
        val result = imageProcessingService.compressImageFromUrl(imageUrl, 80)

        // 결과 값을 검증합니다.
        assertTrue(result.size > 0)

        // 압축된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/compressed_image.jpg"
        val compressedFileBytes = Base64.getDecoder().decode(result.buffer)
        imageProcessingService.saveProcessedFile(compressedFileBytes, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

        // 테스트 후 파일을 삭제합니다.
        Files.deleteIfExists(file.toPath())

        println()
    }

    @Test
    fun `test getImageInfo`() = runBlocking  {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://via.placeholder.com/150"

        // 이미지 URL에서 이미지를 다운로드하여 바이트 배열로 변환합니다.
        val imageBytes = URL(imageUrl).readBytes()

        // 이미지 정보를 가져오는 메서드를 호출합니다.
        val result = imageProcessingService.getImageInfo(imageBytes)

        // 예상 결과 값을 설정합니다.
        val expectedWidth = 150
        val expectedHeight = 150

        // 결과 값을 검증합니다.
        assertEquals(expectedWidth, result.width)
        assertEquals(expectedHeight, result.height)
        assertTrue(result.size > 0)
    }

    @Test
    fun `test saveProcessedFile`() = runBlocking  {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://via.placeholder.com/150"

        // 이미지 URL에서 이미지를 다운로드하여 바이트 배열로 변환합니다.
        val imageBytes = URL(imageUrl).readBytes()

        // 저장할 파일 경로를 설정합니다.
        val outputPath = "/Users/ysk/test/output.jpg"

        // 이미지를 파일로 저장하는 메서드를 호출합니다.
        imageProcessingService.saveProcessedFile(imageBytes, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

        // 테스트 후 파일을 삭제합니다.
        Files.deleteIfExists(file.toPath())
        println()
    }

}
