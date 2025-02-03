package com.yscorp.compression

import net.jpountz.lz4.LZ4Factory
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream
import org.xerial.snappy.Snappy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object CompressionUtils {

    private val lz4Factory = LZ4Factory.fastestInstance()
    private val lz4Compressor = lz4Factory.fastCompressor()
    private val lz4Decompressor = lz4Factory.fastDecompressor()

    fun compressLZ4(data: ByteArray): ByteArray {
        val maxCompressedLength = lz4Compressor.maxCompressedLength(data.size)
        val compressed = ByteArray(maxCompressedLength)
        val compressedSize = lz4Compressor.compress(data, 0, data.size, compressed, 0, maxCompressedLength)
        return compressed.copyOf(compressedSize)
    }

    fun decompressLZ4(compressed: ByteArray, originalSize: Int): ByteArray {
        val restored = ByteArray(originalSize)
        lz4Decompressor.decompress(compressed, 0, restored, 0, originalSize)
        return restored
    }

    fun compressSnappy(data: ByteArray): ByteArray {
        return Snappy.compress(data)
    }

    fun decompressSnappy(compressed: ByteArray): ByteArray {
        return Snappy.uncompress(compressed)
    }

    fun compressLZMA(data: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        LZMACompressorOutputStream(baos).use { it.write(data) }
        return baos.toByteArray()
    }

    fun decompressLZMA(compressed: ByteArray): ByteArray {
        val bais = ByteArrayInputStream(compressed)
        LZMACompressorInputStream(bais).use { return it.readBytes() }
    }

}