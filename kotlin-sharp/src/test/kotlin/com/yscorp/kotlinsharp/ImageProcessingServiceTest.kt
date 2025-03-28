package com.yscorp.kotlinsharp

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
import java.nio.file.Paths
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
    fun home() {
        val userHome = System.getProperty("user.home")

        println(Paths.get(userHome, "node-image", "node_modules").toAbsolutePath().toString())
    }

    @Test
    fun imageInfo () = runBlocking {

        mutableListOf("https://cdn.imweb.me/thumbnail/20221108/6be5b00277948.jpg",
            "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/550197/0GE2VBEP7GNQB-1661c354b2e34830a4ec618bb5dd9612.jpeg",
            "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/550197/0GE2VBEPFGNQD-25195d47bc7a4d7c86482fefc9211daf.jpeg",
            "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/547040/0GE0VF1Q3GJYH-bac35dbeb0c14ab99dad563e3fe194e8.jpeg",
            "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/547040/0GE0VF1PZGJYG-ff31d06473db4a0a975cb4f5a8c0f65b.jpeg"
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

        println()
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
