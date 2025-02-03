package com.yscorp.compression

import com.github.luben.zstd.Zstd
import java.io.File
import java.nio.charset.StandardCharsets

object ZstdJniUtil {

    // 문자열 데이터를 Zstd로 압축하는 함수
    fun compress(data: String): ByteArray {
        val inputData = data.toByteArray(StandardCharsets.UTF_8)
        return Zstd.compress(inputData)
    }

    // Zstd로 압축된 데이터를 해제하는 함수
    fun decompress(compressedData: ByteArray): String {
        // 먼저 압축 해제될 데이터의 예상 최대 크기를 결정
        val decompressedBuffer = ByteArray(1024 * 1024) // 임의로 1MB 버퍼 설정 (필요시 조정 가능)

        // 압축 해제 실행
        val decompressedSize = Zstd.decompress(decompressedBuffer, compressedData)

        // 실제 압축 해제된 데이터만 반환
        return decompressedBuffer.copyOf(decompressedSize.toInt()).toString(StandardCharsets.UTF_8)
    }

    fun decompress(originalData: String) : String {
        return decompress(hexStringToByteArray(originalData.removePrefix("0x")))
    }

    // 바이트 배열 데이터를 Zstd로 압축하는 함수
    fun compressBytes(data: ByteArray, level: Int = 3): ByteArray {
        return Zstd.compress(data, level)
    }

    // Zstd로 압축된 바이트 배열을 해제하는 함수
    fun decompressBytes(compressedData: ByteArray): ByteArray {
        val decompressedBuffer = ByteArray(estimateDecompressedSize(compressedData).toInt())
        val decompressedSize = Zstd.decompress(decompressedBuffer, compressedData)
        return decompressedBuffer.copyOf(decompressedSize.toInt())
    }


    // 파일을 Zstd로 압축하는 함수 (인메모리 방식)
    fun compressFile(inputFile: File, level: Int = 3): ByteArray {
        inputFile.inputStream().use { inputStream ->
            val inputData = inputStream.readBytes()
            return Zstd.compress(inputData, level)
        }
    }

    // Zstd로 압축된 파일을 해제하는 함수 (인메모리 방식)
    fun decompressFile(compressedData: ByteArray): ByteArray {
        val decompressedBuffer = ByteArray(estimateDecompressedSize(compressedData).toInt())
        val decompressedSize = Zstd.decompress(decompressedBuffer, compressedData)
        return decompressedBuffer.copyOf(decompressedSize.toInt())
    }

    // 데이터 압축 크기 예측 함수
    fun estimateCompressedSize(data: ByteArray, level: Int = 3): Long {
        return Zstd.compressBound(data.size.toLong())
    }

    // 압축된 데이터의 예상 압축 해제 크기 반환 함수
    fun estimateDecompressedSize(compressedData: ByteArray): Long {
        return try {
            Zstd.getFrameContentSize(compressedData)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unable to estimate decompressed size: ${e.message}")
        }
    }

    // 압축률 계산 함수
    fun calculateCompressionRatio(originalSize: Long, compressedSize: Long): Double {
        return if (originalSize == 0L) 0.0 else originalSize.toDouble() / compressedSize.toDouble()
    }

    // Zstd로 압축된 데이터인지 확인하는 함수
    fun isCompressed(data: ByteArray): Boolean {
        return try {
            estimateDecompressedSize(data) >= 0
        } catch (e: Exception) {
            false
        }
    }

    private  fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }
}