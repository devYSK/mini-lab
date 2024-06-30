package com.yscorp.kotlinsharp

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class ImageProcessServiceTest {
    private val imageProcessService = ImageProcessService()

    @Test
    fun `test compressImageFromUrl with stream`() = runBlocking{
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/547040/0GE0VF1PZGJYG-ff31d06473db4a0a975cb4f5a8c0f65b.jpeg";

        // URL에서 이미지를 압축하는 메서드를 호출합니다.
        val result = imageProcessService.compressImageFromUrl(imageUrl, 30)

        // 결과 값을 검증합니다.
        assertTrue(result.size > 0)

        // 압축된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/compressed_image.jpg"
        imageProcessService.saveProcessedFile(result.buffer, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())

//        // 테스트 후 파일을 삭제합니다.
//        Files.deleteIfExists(file.toPath())
    }

    @Test
    fun `test processImageFromUrl with stream`() = runBlocking {
        // 테스트할 이미지 URL을 설정합니다.
        val imageUrl = "https://ffm-blog-s3.s3.ap-northeast-2.amazonaws.com/grow/prod/547040/0GE0VF1PZGJYG-ff31d06473db4a0a975cb4f5a8c0f65b.jpeg";

        // URL에서 이미지를 처리하는 메서드를 호출합니다.
        val result = imageProcessService.processImageFromUrl(imageUrl, "jpg", 1000, 1000, true)

        // 처리된 이미지를 /Users/ysk/test/ 디렉토리에 저장합니다.
        val outputPath = "/Users/ysk/test/processed_image2.jpg"
        imageProcessService.saveProcessedFile(result.buffer, outputPath)

        // 파일이 존재하는지 검증합니다.
        val file = File(outputPath)
        assertTrue(file.exists())
    }

}