package com.yscorp.compression
import java.math.BigDecimal
import java.math.RoundingMode

object UnitConversionUtils {

    private val KB = BigDecimal(1024)
    private val MB = KB.multiply(KB)
    private val GB = MB.multiply(KB)
    private val TB = GB.multiply(KB)
    private val PB = TB.multiply(KB)

    /**
     * 바이트 값을 킬로바이트로 변환
     */
    fun bytesToKilobytes(bytes: Long): Double {
        return BigDecimal(bytes).divide(KB, 6, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * 바이트 값을 메가바이트로 변환
     */
    fun bytesToMegabytes(bytes: Long): Double {
        return BigDecimal(bytes).divide(MB, 6, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * 바이트 값을 기가바이트로 변환
     */
    fun bytesToGigabytes(bytes: Long): Double {
        return BigDecimal(bytes).divide(GB, 6, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * 킬로바이트 값을 바이트로 변환
     */
    fun kilobytesToBytes(kilobytes: Double): Long {
        return BigDecimal(kilobytes).multiply(KB).longValueExact()
    }

    /**
     * 메가바이트 값을 바이트로 변환
     */
    fun megabytesToBytes(megabytes: Double): Long {
        return BigDecimal(megabytes).multiply(MB).longValueExact()
    }

    /**
     * 기가바이트 값을 바이트로 변환
     */
    fun gigabytesToBytes(gigabytes: Double): Long {
        return BigDecimal(gigabytes).multiply(GB).longValueExact()
    }

}
