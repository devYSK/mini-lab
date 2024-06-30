package com.yscorp.kotlinsharp

object ByteUtils {

    fun getFileSizeInKB(byteArray: ByteArray): Double {
        return byteArray.size / 1024.0
    }

    private const val KILOBYTE: Long = 1024
    private const val MEGABYTE: Long = KILOBYTE * 1024
    private const val GIGABYTE: Long = MEGABYTE * 1024
    private const val TERABYTE: Long = GIGABYTE * 1024

    fun bytesToKilobytes(bytes: Long): Double {
        return bytes.toDouble() / KILOBYTE
    }

    fun bytesToMegabytes(bytes: Long): Double {
        return bytes.toDouble() / MEGABYTE
    }

    fun bytesToGigabytes(bytes: Long): Double {
        return bytes.toDouble() / GIGABYTE
    }

    fun bytesToTerabytes(bytes: Long): Double {
        return bytes.toDouble() / TERABYTE
    }

    fun kilobytesToBytes(kilobytes: Double): Long {
        return (kilobytes * KILOBYTE).toLong()
    }

    fun megabytesToBytes(megabytes: Double): Long {
        return (megabytes * MEGABYTE).toLong()
    }

    fun gigabytesToBytes(gigabytes: Double): Long {
        return (gigabytes * GIGABYTE).toLong()
    }

    fun terabytesToBytes(terabytes: Double): Long {
        return (terabytes * TERABYTE).toLong()
    }

    fun kilobytesToMegabytes(kilobytes: Double): Double {
        return kilobytes / KILOBYTE
    }

    fun kilobytesToGigabytes(kilobytes: Double): Double {
        return kilobytes / MEGABYTE
    }

    fun kilobytesToTerabytes(kilobytes: Double): Double {
        return kilobytes / GIGABYTE
    }

    fun megabytesToKilobytes(megabytes: Double): Double {
        return megabytes * KILOBYTE
    }

    fun megabytesToGigabytes(megabytes: Double): Double {
        return megabytes / KILOBYTE
    }

    fun megabytesToTerabytes(megabytes: Double): Double {
        return megabytes / MEGABYTE
    }

    fun gigabytesToKilobytes(gigabytes: Double): Double {
        return gigabytes * MEGABYTE
    }

    fun gigabytesToMegabytes(gigabytes: Double): Double {
        return gigabytes * KILOBYTE
    }

    fun gigabytesToTerabytes(gigabytes: Double): Double {
        return gigabytes / KILOBYTE
    }

    fun terabytesToKilobytes(terabytes: Double): Double {
        return terabytes * GIGABYTE
    }

    fun terabytesToMegabytes(terabytes: Double): Double {
        return terabytes * MEGABYTE
    }

    fun terabytesToGigabytes(terabytes: Double): Double {
        return terabytes * KILOBYTE
    }

}
