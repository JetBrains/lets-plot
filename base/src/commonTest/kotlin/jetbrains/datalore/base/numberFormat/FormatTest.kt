package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTest {
    @Test
    fun toFixed() {
        assertEquals("0", Format.toFixed(0.0, 0))
        assertEquals("0", Format.toFixed(0.49, 0))
        assertEquals("0", Format.toFixed(0.5, 0))
        assertEquals("1", Format.toFixed(0.51, 0))
        assertEquals("0", Format.toFixed(-0.49, 0))
        assertEquals("0", Format.toFixed(-0.5, 0))
        assertEquals("-1", Format.toFixed(-0.51, 0))
        assertEquals("0.0", Format.toFixed(0.0, 1))
        assertEquals("0.4", Format.toFixed(0.449, 1))
        assertEquals("0.4", Format.toFixed(0.45, 1))
        assertEquals("0.5", Format.toFixed(0.451, 1))
        assertEquals("0.5", Format.toFixed(0.5, 1))
        assertEquals("0.5", Format.toFixed(0.549, 1))
        assertEquals("0.6", Format.toFixed(0.55, 1))
        assertEquals("0.6", Format.toFixed(0.551, 1))
        assertEquals("0.0000", Format.toFixed(0.0, 4))
        assertEquals("0.0000", Format.toFixed(-0.0, 4))
        assertEquals("0", Format.toFixed(0.0))
        assertEquals("0", Format.toFixed(-0.0))
        assertEquals("1235", Format.toFixed(1234.567))
        assertEquals("1235", Format.toFixed(1234.567, 0))
        assertEquals("1234.6", Format.toFixed(1234.567, 1))
        assertEquals("1234.57", Format.toFixed(1234.567, 2))
        assertEquals("1234.56700", Format.toFixed(1234.567, 5))
    }

    @Test
    fun toExponential() {
        assertEquals("0.0000e+0", Format.toExponential(0.0, 4))
        assertEquals("0.0000e+0", Format.toExponential(-0.0, 4))
        assertEquals("0e+0", Format.toExponential(0.0))
        assertEquals("0e+0", Format.toExponential(-0.0))
        assertEquals("1.23456e+2", Format.toExponential(123.456))
        assertEquals("1e+2", Format.toExponential(123.456, 0))
        assertEquals("1.2e+2", Format.toExponential(123.456, 1))
        assertEquals("1.23e+2", Format.toExponential(123.456, 2))
        assertEquals("1.235e+2", Format.toExponential(123.456, 3))
        assertEquals("1.23456e+2", Format.toExponential(123.456, 5))
        assertEquals("1.234560e+2", Format.toExponential(123.456, 6))
        assertEquals("4.56e-3", Format.toExponential(0.00456))
        assertEquals("1e-2", Format.toExponential(0.01))
        assertEquals("1e-1", Format.toExponential(0.1))
        assertEquals("9e-1", Format.toExponential(0.9))
        assertEquals("9.999e-1", Format.toExponential(0.9999))
        assertEquals("1.00e+0", Format.toExponential(0.9999, 2))
    }

    @Test
    fun toPrecision() {
        assertEquals("1", Format.toPrecision(0.999, 1))
        assertEquals("1.0", Format.toPrecision(0.999, 2))
        assertEquals("0.999", Format.toPrecision(0.999, 3))
        assertEquals("0.000", Format.toPrecision(0.0, 4))
        assertEquals("0.000", Format.toPrecision(-0.0, 4))
        assertEquals("0", Format.toPrecision(0.0))
        assertEquals("0", Format.toPrecision(-0.0))
        assertEquals("1234.567", Format.toPrecision(1234.567))
        assertEquals("1e+3", Format.toPrecision(1234.567, 1))
        assertEquals("1.2e+3", Format.toPrecision(1234.567, 2))
        assertEquals("1234.6", Format.toPrecision(1234.567, 5))
        assertEquals("1234.567000", Format.toPrecision(1234.567, 10))
    }
}