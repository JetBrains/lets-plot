/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.Companion.MULT_SIGN
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.Companion.siPrefixFromExponent
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import kotlin.math.sign

internal object FormatNotationUtil {

    internal fun formatExponentNotation(
        number: NormalizedFloat,
        precision: Int,
        minExp: Int,
        maxExp: Int,
        expType: ExponentNotationType
    ): FormattedNumber {
        fun buildExponentString(exp: Int) =
            when (expType) {
                ExponentNotationType.E -> "e${if (exp.sign >= 0) "+" else ""}$exp"
                ExponentNotationType.POW, ExponentNotationType.POW_FULL -> when {
                    exp == 0 && minExp < 0 && maxExp > 0 -> ""
                    exp == 1 && minExp < 1 && maxExp > 1 -> "${MULT_SIGN}10"
                    else -> "$MULT_SIGN\\(10^{$exp}\\)"
                }
            }

        if (precision > -1) {
            val rounded = number.toPrecision(precision)
            val (significand, fraction) = rounded.formatScientificStr(precision)
            return FormattedNumber(
                integerPart = significand,
                fractionalPart = fraction,
                exponentialPart = buildExponentString(rounded.exponent),
                expType = expType
            )
        } else {
            if (number == NormalizedFloat.ZERO) {
                // Zero should be formatted as "0" instead of "0e+0"
                return FormattedNumber("0", "", "")
            }

            // Format without ".0" fractional part when number with one significant digit (e.g., 1.0, 0.1, 0.0004)
            // 0.0 -> "0"
            // 1.0 -> "1e+0"
            // 0.1 -> "1e-1"

            val (significand, fraction) = number.formatScientificStr(precision)
            return FormattedNumber(
                integerPart = significand,
                fractionalPart = fraction.takeIf { it != "0" } ?: "", // 1.0e0 -> 1e0
                exponentialPart = buildExponentString(number.exponent),
                expType = expType
            )
        }
    }

    internal fun formatSiNotation(number: NormalizedFloat, precision: Int): FormattedNumber {
        val significantDigitsPrecision = maxOf(0, precision - 1) // round all, except the first digit
        val rounded = number.toPrecision(significantDigitsPrecision)
        val siPrefix = siPrefixFromExponent(rounded.exponent)
        val siScaledNumber = rounded.shiftDecimalPoint(-siPrefix.baseExponent) // 1.0 <= wholeValue < 1000.0
        val decimalPartPrecision = if (siScaledNumber.exponent >= 0) {
            // Precision in si format is the number of significant digits including the whole part.
            // Decimal part precision is the number of digits after the whole part.
            significantDigitsPrecision - siScaledNumber.wholePartLength
        } else {
            // Number is less than 1.0. Apply the full precision to the decimal part.
            significantDigitsPrecision
        }

        return formatDecimalNotation(siScaledNumber, decimalPartPrecision).copy(exponentialPart = siPrefix.symbol)
    }

    // (9.925, 0) -> "10"
    // (123.925, 0) -> "124"
    // (1.925, 6) -> "1.925000"
    // (1.925, 2) -> "1.93"
    // (12345678, 2) -> "12345678.00"
    // (0.00001234, 2) -> "0"
    internal fun formatDecimalNotation(number: NormalizedFloat, precision: Int): FormattedNumber {
        val (integerPart, d) = number.toDecimalPrecision(precision).formatDecimalStr(precision)
        val fractionalPart = if (precision <= 0) "" else d
        return FormattedNumber(integerPart, fractionalPart)
    }

    internal fun formatGeneralNotation(
        number: NormalizedFloat,
        precision: Int,
        minExp: Int,
        maxExp: Int,
        expType: ExponentNotationType
    ): FormattedNumber {
        // precision = 0 and maxExp = 0 forces all numbers to be formatted in exponential notation.
        // Override maxExp to format single digit numbers as decimals  (5 -> "5" instead of "5e+0").
        val maxExp = maxExp.takeUnless { precision == 0 && it == 0 } ?: 1

        val significantDigitsCount = maxOf(0, precision - 1) // exclude significand
        val rounded = number.toPrecision(significantDigitsCount)

        return if (rounded.exponent > minExp && rounded.exponent < maxExp) {
            // precision is
            val (integerPart, fractionalPart) = rounded.formatDecimalStr(significantDigitsCount - rounded.exponent)
            FormattedNumber(integerPart, fractionalPart)
        } else {
            formatExponentNotation(rounded, significantDigitsCount, minExp, maxExp, expType)
        }
    }
}
