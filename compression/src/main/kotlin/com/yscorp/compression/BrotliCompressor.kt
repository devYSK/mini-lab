package com.yscorp.compression

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.decoder.DirectDecompress
import com.aayushatharva.brotli4j.encoder.Encoder

object BrotliCompressor {

    init {
        // Brotli4j 네이티브 라이브러리 로드
        Brotli4jLoader.ensureAvailability()
    }

    fun compress(input: ByteArray): ByteArray {
        return Encoder.compress(input)
    }

    fun decompress(input: ByteArray): ByteArray {
        val decompressed = DirectDecompress.decompress(input)
        if (decompressed.resultStatus == com.aayushatharva.brotli4j.decoder.DecoderJNI.Status.DONE) {
            return decompressed.decompressedData
        } else {
            throw RuntimeException("Brotli decompression failed: ${decompressed.resultStatus}")
        }
    }
}