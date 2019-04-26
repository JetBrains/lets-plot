package jetbrains.datalore.base.numberFormat

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberFormatUtilTest {
    @Test
    fun numberFormat() {
        assertEquals("12,345.68", NumberFormatUtil.formatNumber(12345.6789, "#,##0.00"))
        assertEquals("12345.7", NumberFormatUtil.formatNumber(12345.6789, "#0.0"))
        assertEquals("0.0", NumberFormatUtil.formatNumber(1.23456719e-10, "#,##0.0"))
        assertEquals("NaN", NumberFormatUtil.formatNumber(java.lang.Double.NaN, "#,##0.0"))
    }

    @Test
    fun negativeZero() {
        assertEquals("0.0", NumberFormatUtil.formatNumber(-0.01, "#,##0.0"))
    }
}