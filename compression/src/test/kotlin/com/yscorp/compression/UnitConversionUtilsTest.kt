package com.yscorp.compression

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class UnitConversionUtilsTest {

    @Test
    fun `바이트를 킬로바이트로 변환`() {
        assertEquals(1.0, UnitConversionUtils.bytesToKilobytes(1024))
        assertEquals(0.000977, UnitConversionUtils.bytesToKilobytes(1), 0.000001)
    }

    @Test
    fun `바이트를 메가바이트로 변환`() {
        assertEquals(1.0, UnitConversionUtils.bytesToMegabytes(1024 * 1024))
        assertEquals(0.000001, UnitConversionUtils.bytesToMegabytes(1), 0.000001)
    }

    @Test
    fun `바이트를 기가바이트로 변환`() {
        assertEquals(1.0, UnitConversionUtils.bytesToGigabytes(1024 * 1024 * 1024))
        assertEquals(0.000000001, UnitConversionUtils.bytesToGigabytes(1), 0.000000001)
    }

    @Test
    fun `킬로바이트를 바이트로 변환`() {
        assertEquals(1024, UnitConversionUtils.kilobytesToBytes(1.0))
        assertEquals(1536, UnitConversionUtils.kilobytesToBytes(1.5))
    }

    @Test
    fun `메가바이트를 바이트로 변환`() {
        assertEquals(1024 * 1024, UnitConversionUtils.megabytesToBytes(1.0))
        assertEquals(1024 * 1024 + 512 * 1024, UnitConversionUtils.megabytesToBytes(1.5))
    }

    @Test
    fun `기가바이트를 바이트로 변환`() {
        assertEquals(1024L * 1024 * 1024, UnitConversionUtils.gigabytesToBytes(1.0))
        assertEquals(1024L * 1024 * 1024 + 512L * 1024 * 1024, UnitConversionUtils.gigabytesToBytes(1.5))
    }

}
