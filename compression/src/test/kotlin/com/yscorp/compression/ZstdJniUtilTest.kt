package com.yscorp.compression

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class ZstdJniUtilTest {
    // 테스트할 문자열 (대량의 데이터로 테스트하기 위해 임의로 큰 문자열을 생성)
    private val testData = "This is a large test string for Zstd compression in Kotlin.".repeat(1000)


    @Test
    fun testCompressionAndDecompression1234234() {
        val originalData = "0x28B52FFD4400ED02CD0C0002D85027E0CCCC0008D94A216440C8ECA85B230D85D3CF5334D4D70FD41992B44F656CC41E31C61806F970009D0707CAC54F5F74B85D3E747C209268383CCEBEE0B46907835E92EBE061299747F351AEC2E1B00AE1D2BA5AE02CF78C85687A8585E8FF0642394B12595C228365CC7438263008E67F9D0F5DC5B4DCAC2AB4ABB8DA143D1295A6676E5B7DEED4B4075B72266D196E30AC49A3A042B0A097DD59066A7555188E5AA0B527A91A5C838A02795C3772272176C14C3CF029AF69F80888800CF2B82A05D6D73B5988CEFAD29CF3D960489735B9132B9AFEA4FFE846AF69D9F1088335C12414ABE0A81A16990E6B5CE64C38529EC34C96F3B7479D903D2CBA7401A71C3362D0A52F30CC29CF1EFE5C66B1C31BE59CEE3B00C711E3924EF4230B4DFA758AAC9566A8C14D76E9DECEA0306BF00739E59FC6DED67CC28172316B88BD339F081E000B290795F685173020528E43C698C95D0A58300864242BA0B42DDF8EF63C9C85D7672261CEBA4259D9825859946CF80DAB43C420742404CC60F38CE1165479DB706CC130E656A062D58C197E9E49B1941EFF4CD63D"
//        val compressedData = ZstdJniUtil.compress(originalData)
        val decompressedData = ZstdJniUtil.decompress(hexStringToByteArray(originalData.removePrefix("0x")))

        println(decompressedData)
//
//        assertEquals(originalData, decompressedData)
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }

    @Test
    fun testCompressionAndDecompressionPerformance() {
        // 압축 전 데이터 크기
        val originalSize = testData.toByteArray(StandardCharsets.UTF_8).size
        println("원본 데이터 크기: $originalSize bytes")
        var compressedData = byteArrayOf()

        // 압축 시간 측정
        val compressTime = measureTimeMillis {
            compressedData = ZstdJniUtil.compress(testData)

            // 압축된 데이터 크기
            val compressedSize = compressedData.size
            println("압축된 데이터 크기: $compressedSize bytes")

            // 압축된 데이터가 원본보다 작아야 함
            assertTrue(compressedSize < originalSize, "압축된 데이터가 원본 데이터보다 작아야 합니다")
        }

        // 압축 해제 시간 측정
        val decompressTime = measureTimeMillis {
            val decompressedData = ZstdJniUtil.decompress(compressedData)
            assertEquals(testData, decompressedData, "압축 해제된 데이터가 원본 데이터와 일치해야 합니다")
        }

        // 압축 및 해제 시간 출력
        println("압축 시간: $compressTime ms")
        println("압축 해제 시간: $decompressTime ms")
    }

    @Test
    fun `Zstd 압축 및 해제 테스트`() {
        // 테스트할 문자열 데이터
        val originalData = "압축 테스트 데이터를 생성합니다. 데이터가 크면 클수록 압축 성능이 더 눈에 띄게 개선됩니다. " +
            "이 테스트는 Zstd JNI 라이브러리의 압축 성능과 속도를 측정하기 위한 테스트입니다."

        // 압축 성능 및 속도 테스트
        var compressedData: ByteArray
        val compressTime = measureTimeMillis {
            compressedData = ZstdJniUtil.compress(originalData)
        }
        println("압축된 데이터 크기: ${compressedData.size} bytes")
        println("압축 시간: $compressTime ms")

        // 압축 해제 성능 및 속도 테스트
        var decompressedData: String
        val decompressTime = measureTimeMillis {
            decompressedData = ZstdJniUtil.decompress(compressedData)
        }
        println("압축 해제 시간: $decompressTime ms")

        // 압축 해제 후 원본 데이터와 비교
        assertEquals(originalData, decompressedData, "압축 해제된 데이터가 원본과 일치하지 않습니다.")
        println("압축 해제 후 데이터가 원본과 일치합니다.")
    }

    @Test
    fun `큰 데이터에 대한 Zstd 압축 및 해제 테스트`() {
        // 매우 큰 데이터 생성
        val largeData = "테스트".repeat(100000)

        // 큰 데이터 압축 성능 및 속도 테스트
        var compressedData: ByteArray
        val compressTime = measureTimeMillis {
            compressedData = ZstdJniUtil.compress(largeData)
        }
        println("큰 데이터 압축 크기: ${compressedData.size} bytes")
        println("큰 데이터 압축 시간: $compressTime ms")

        // 큰 데이터 압축 해제 성능 및 속도 테스트
        var decompressedData: String
        val decompressTime = measureTimeMillis {
            decompressedData = ZstdJniUtil.decompress(compressedData)
        }
        println("큰 데이터 압축 해제 시간: $decompressTime ms")

        // 압축 해제 후 원본 데이터와 비교
        assertEquals(largeData, decompressedData, "큰 데이터 압축 해제된 데이터가 원본과 일치하지 않습니다.")
        println("큰 데이터 압축 해제 후 데이터가 원본과 일치합니다.")
    }

    @Test
    fun testCompressAndDecompress() {
        val originalString = "This is a test string for compression and decompression."
        val compressedData = ZstdJniUtil.compress(originalString)
        val decompressedString = ZstdJniUtil.decompress(compressedData)
        assertEquals(originalString, decompressedString)
    }

    @Test
    fun testCompressBytesAndDecompressBytes() {
        val originalBytes = "Test Byte Array".toByteArray()
        val compressedBytes = ZstdJniUtil.compressBytes(originalBytes)
        val decompressedBytes = ZstdJniUtil.decompressBytes(compressedBytes)
        assertArrayEquals(originalBytes, decompressedBytes)
    }

    @Test
    fun testCompressFileAndDecompressFile() {
        val tempFile = File.createTempFile("testFile", ".txt")
        tempFile.writeText("This is a test file for compression and decompression.")
        val compressedData = ZstdJniUtil.compressFile(tempFile)
        val decompressedData = ZstdJniUtil.decompressFile(compressedData)
        assertEquals(tempFile.readText(), decompressedData.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun testEstimateCompressedSize() {
        val originalBytes = "Estimate compressed size test".toByteArray()
        val estimatedSize = ZstdJniUtil.estimateCompressedSize(originalBytes)
        assertTrue(estimatedSize > 0)
    }

    @Test
    fun testCalculateCompressionRatio() {
        val originalSize = 1000L
        val compressedSize = 500L
        val ratio = ZstdJniUtil.calculateCompressionRatio(originalSize, compressedSize)
        assertEquals(2.0, ratio, 0.01)
    }

    @Test
    fun testIsCompressed() {
        val originalString = "Is this compressed?"
        val compressedData = ZstdJniUtil.compress(originalString)
        assertTrue(ZstdJniUtil.isCompressed(compressedData))
        assertFalse(ZstdJniUtil.isCompressed(originalString.toByteArray()))
    }
}