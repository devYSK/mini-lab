package com.yscorp.kotlinsharp

import com.example.imagedemo.ImageProcessingService2
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.net.URL
import java.nio.file.Files

@SpringBootTest
class ImageProcessingService2Test {

    private val imageProcessingService = ImageProcessingService2()

    @Test
    fun `test processImageFromUrl with stream`() {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/547040/0GE0VF1PZGJYG-ff31d06473db4a0a975cb4f5a8c0f65b.jpeg"

        // URL에서 이미지를 처리하는 메서드를 호출합니다.
        val result = imageProcessingService.processImageFromUrl(imageUrl, "jpg", 100, 100, false)

        // 예상 결과 값을 설정합니다.
        val expectedWidth = 100
        val expectedHeight = 100

        // 결과 값을 검증합니다.
        assertEquals(expectedWidth, result.width)
        assertEquals(expectedHeight, result.height)
        assertTrue(result.size > 0)

        // 처리된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/processed_image2.jpg"
        imageProcessingService.saveProcessedFile(result.buffer, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

        // 테스트 후 파일을 삭제합니다.
        Files.deleteIfExists(file.toPath())
    }

    @Test
    fun `test compressImageFromUrl with stream`() {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://via.placeholder.com/150"

        // URL에서 이미지를 압축하는 메서드를 호출합니다.
        val result = imageProcessingService.compressImageFromUrl(imageUrl, 80)

        // 결과 값을 검증합니다.
        assertTrue(result.size > 0)

        // 압축된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/compressed_image.jpg"
        imageProcessingService.saveProcessedFile(result.buffer, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

//        // 테스트 후 파일을 삭제합니다.
//        Files.deleteIfExists(file.toPath())
    }

    @Test
    fun `test getImageInfo with stream`() {
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
    fun `test saveProcessedFile`() {
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
    }
}
