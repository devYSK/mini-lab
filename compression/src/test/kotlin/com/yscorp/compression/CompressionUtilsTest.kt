package com.yscorp.compression

import org.junit.jupiter.api.Assertions.*
import kotlin.system.measureNanoTime
import kotlin.test.Test

class CompressionUtilsTest {
    private val dataSizes = listOf(1 * 1024 * 1024, 5 * 1024 * 1024, 10 * 1024 * 1024) // 1MB, 5MB, 10MB

    @Test
    fun testLZ4Compression() {
        dataSizes.forEach { size ->
            val data = generateTestData(size)
            println("LZ4 테스트 - 데이터 크기: $size bytes")

            lateinit var compressedData: ByteArray
            val compressTime = measureNanoTime {
                compressedData = CompressionUtils.compressLZ4(data)
            }
            val compressedSize = compressedData.size

            lateinit var decompressedData: ByteArray
            val decompressTime = measureNanoTime {
                decompressedData = CompressionUtils.decompressLZ4(compressedData, data.size)
            }

            println("압축 시간: ${compressTime / 1_000_000} ms")
            println("압축 후 크기: $compressedSize bytes")
            println("복원 시간: ${decompressTime / 1_000_000} ms")

            assertArrayEquals(data, decompressedData)
            println("복원 성공: 원본 데이터와 일치합니다.\n")
        }
    }

    @Test
    fun testSnappyCompression() {
        dataSizes.forEach { size ->
            val data = generateTestData(size)
            println("Snappy 테스트 - 데이터 크기: $size bytes")

            lateinit var compressedData: ByteArray
            val compressTime = measureNanoTime {
                compressedData = CompressionUtils.compressSnappy(data)
            }
            val compressedSize = compressedData.size

            lateinit var decompressedData: ByteArray
            val decompressTime = measureNanoTime {
                decompressedData = CompressionUtils.decompressSnappy(compressedData)
            }

            println("압축 시간: ${compressTime / 1_000_000} ms")
            println("압축 후 크기: $compressedSize bytes")
            println("복원 시간: ${decompressTime / 1_000_000} ms")

            assertArrayEquals(data, decompressedData)
            println("복원 성공: 원본 데이터와 일치합니다.\n")
        }
    }

    @Test
    fun testLZMACompression() {
        dataSizes.forEach { size ->
            val data = generateTestData(size)
            println("LZO 테스트 - 데이터 크기: $size bytes")

            lateinit var compressedData: ByteArray
            val compressTime = measureNanoTime {
                compressedData = CompressionUtils.compressLZMA(data)
            }
            val compressedSize = compressedData.size

            lateinit var decompressedData: ByteArray
            val decompressTime = measureNanoTime {
                decompressedData = CompressionUtils.compressLZMA(compressedData)
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