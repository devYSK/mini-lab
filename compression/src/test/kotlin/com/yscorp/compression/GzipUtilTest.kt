package com.yscorp.compression

import org.junit.jupiter.api.Assertions.*
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class GzipUtilTest {
    // 테스트할 문자열 (대량의 데이터로 테스트하기 위해 임의로 큰 문자열을 생성)
    private val testData = "This is a large test string for Gzip compression in Kotlin.".repeat(1000)

    @Test
    fun testCompressionAndDecompressionPerformance() {
        // 압축 전 데이터 크기
        val originalSize = testData.toByteArray(Charsets.UTF_8).size
        println("원본 데이터 크기: $originalSize bytes")

        var compressedData = byteArrayOf()

        // 압축 시간 측정
        val compressTime = measureTimeMillis {
            compressedData = GzipUtil.compress(testData)

            // 압축된 데이터 크기
            val compressedSize = compressedData.size
            println("압축된 데이터 크기: $compressedSize bytes")

            // 압축된 데이터가 원본보다 작아야 함
            assertTrue(compressedSize < originalSize, "압축된 데이터가 원본 데이터보다 작아야 합니다")
        }


        // 압축 해제 시간 측정
        val decompressTime = measureTimeMillis {
            val decompressedData = GzipUtil.decompress(compressedData)
            assertEquals(testData, decompressedData, "압축 해제된 데이터가 원본 데이터와 일치해야 합니다")
        }

        // 압축 및 해제 시간 출력
        println("압축 시간: $compressTime ms")
        println("압축 해제 시간: $decompressTime ms")
    }
}