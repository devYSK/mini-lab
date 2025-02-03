package com.yscorp.compression

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GzipUtil {

    // 문자열 데이터를 Gzip으로 압축하는 함수
    fun compress(data: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { gzip ->
            gzip.write(data.toByteArray(Charsets.UTF_8))
        }
        return byteArrayOutputStream.toByteArray()
    }

    // Gzip으로 압축된 데이터를 해제하는 함수
    fun decompress(compressedData: ByteArray): String {
        val byteArrayInputStream = ByteArrayInputStream(compressedData)
        GZIPInputStream(byteArrayInputStream).use { gzip ->
            return gzip.readBytes().toString(Charsets.UTF_8)
        }
    }

}