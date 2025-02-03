package com.yscorp.compression

import org.junit.jupiter.api.Assertions.*
import kotlin.system.measureNanoTime
import kotlin.test.Test

class BrotliCompressorTest {
    private val dataSizes = listOf(1 * 1024 * 1024, 5 * 1024 * 1024, 10 * 1024 * 1024) // 1MB, 5MB, 10MB

    @Test
    fun `test brotli compression and decompression`() {
        val original = "This is a test string for Brotli compression".toByteArray()
        val compressed = BrotliCompressor.compress(original)
        val decompressed = BrotliCompressor.decompress(compressed)

        assertNotEquals(original.size, compressed.size)  // 압축 후 사이즈 다름 확인
        assertArrayEquals(original, decompressed)  // 압축 해제 후 원본과 동일한지 확인
    }

    @Test
    fun testBrotliCompression() {
        dataSizes.forEach { size ->
            val data = generateTestData(size)
            println("Brotli 테스트 - 데이터 크기: $size bytes")

            lateinit var compressedData: ByteArray
            val compressTime = measureNanoTime {
                compressedData = BrotliCompressor.compress(data)
            }
            val compressedSize = compressedData.size

            lateinit var decompressedData: ByteArray
            val decompressTime = measureNanoTime {
                decompressedData = BrotliCompressor.decompress(compressedData)
            }

            println("압축 시간: ${compressTime / 1_000_000} ms")
            println("압축 후 크기: $compressedSize bytes")
            println("복원 시간: ${decompressTime / 1_000_000} ms")

            assertArrayEquals(data, decompressedData)
            println("복원 성공: 원본 데이터와 일치합니다.\n")
        }
    }

    private fun generateTestData(size: Int): ByteArray {
        val data = ByteArray(size)
        for (i in data.indices) {
            data[i] = (i % 256).toByte()
        }
        return data
    }
}