/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.sign

internal class NormalizedFloat private constructor(
    val significand: Int, // 1..9 or 0 for 0.0
    fraction: String, // "0" for zero, never contains trailing zeros
    exponent: Int,
    val sign: String = ""
) {
    val fraction: String = fraction.trimEnd('0').takeIf { it.isNotEmpty() } ?: "0" // never empty
    val exponent: Int = exponent.takeIf { significand != 0 || this.fraction != "0" } ?: 0

    val isNegative = sign == "-"

    init {
        if (this.significand == 0) require(this.fraction == "0") { "i should be in 0..9, but was ${this.significand}" }
        if (this.significand != 0) require(this.significand in 1..9) { "i should be in 0..9, but was ${this.significand}" }
        require(this.fraction == "0" || this.fraction.last() != '0') { "fraction should not end with '0'" }
    }

    // Decimal part of a decimal number:
    // (1.2345678e3) -> 1234.5678 -> "5678"
    val decimalPart: String
        get() = when {
            exponent == 0 -> fraction
            exponent < 0 -> "0".repeat(-exponent - 1) + significand.digitToChar() + (fraction.takeIf { it != "0" } ?: "")
            exponent > 0 -> when (fraction) {
                "0" -> "0"
                else -> fraction.drop(exponent).takeIf(String::isNotEmpty) ?: "0"
            }

            else -> error("Unexpected state: $exponent")
        }

    val wholePartLength = exponent

    // Whole part of a decimal number:
    // (1.2345678e3) -> 1234.5678 -> "1234"
    val wholePart: String
        get() {
            if (exponent < 0) return "0"
            return (significand.toString() + fraction.take(exponent)).padEnd(exponent + 1, '0')
        }

    // Returns the whole part and the decimal part with the specified length.
    // (123.99, -1) -> "123" to "99"
    // (123.99, 0) -> "123" to ""
    // (12.3, 4) -> "12" to "3000"
    // (12.399, 2) -> "12" to "39"
    // (1.0, -1) -> ("1", "0")
    fun formatDecimalStr(decimalPartLength: Int = -1): Pair<String, String> {
        return if (decimalPartLength < 0) {
            wholePart to decimalPart
        } else {
            wholePart to decimalPart.take(decimalPartLength).padEnd(decimalPartLength, '0')
        }
    }

    // Returns the significand (one digit) and the fraction part with the specified length.
    // Negative length returns fraction part as is:
    //  (1.2399e2, -1) -> "1", to "2399"
    // Length greater than the fraction part length pads the fraction part with zeros:
    //  (1.23e1, 4) -> "1", to "2300"
    // Length less than the fraction part length truncates the fraction part:
    //  (1.2399e1, 2) -> "1", to "23"
    fun formatScientificStr(fractionLength: Int = -1): Pair<String, String> {
        return if (fractionLength < 0) {
            significand.toString() to fraction
        } else {
            significand.toString() to fraction.take(fractionLength).padEnd(fractionLength, '0')
        }
    }


    // Adjust precision of the number by rounding it to the specified number of decimal places.
    // (1.2345678e3, 2) => (1234.5678, 2) => 1234.57 => 1.23457e3
    // (1.2345678e3, 0) => (1234.5678, 0) => 1235.0 => 1.235e3
    // If precision is greater than the number of digits in the fraction part, the number is rounded to the nearest integer.
    // (1.23e-10, 3) -> (0.000000000123, 3) -> 0.0000000001 -> 1e-10
    fun toDecimalPrecision(precision: Int): NormalizedFloat {
        return toPrecision(maxOf(0, precision + exponent))
    }

    // Adjust precision of the number by rounding it to the specified number of significant digits.
    // (1.2345678e3, 5) -> 1.23457E3
    // (1.2345678e3, 2) -> 1.2E3
    // (9.9e0, 0) -> 1E1
    fun toPrecision(precision: Int): NormalizedFloat {
        require(precision >= 0) { "Precision should be non-negative, but was $precision" }

        if (precision > fraction.length) {
            return this
        }

        val (roundedFraction, carry) = Arithmetic.round(fraction, precision)

        return if (carry) {
            if (significand == 9) {
                NormalizedFloat(1, "0$roundedFraction", exponent + 1, sign)
            } else {
                NormalizedFloat(significand + 1, roundedFraction, exponent, sign)
            }
        } else {
            NormalizedFloat(significand, roundedFraction, exponent, sign)
        }
    }

    // Positive delta means shift decimal point to the right
    // (1.2345678e3, 2) -> 1234.5678 -> 123456.78 -> 1.2345678e5
    // Negative delta means shift decimal point to the left:
    // (1.2345678e3, -2) -> 1234.5678 -> 12.345678 -> 1.2345678e1
    fun shiftDecimalPoint(delta: Int): NormalizedFloat {
        return NormalizedFloat(significand, fraction, exponent + delta, sign)
    }


    override fun toString(): String {
        return "Floating(i=$significand, fraction='$fraction', e=${exponent})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NormalizedFloat

        if (significand != other.significand) return false
        if (exponent != other.exponent) return false
        if (sign != other.sign) return false
        if (fraction != other.fraction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = significand
        result = 31 * result + exponent
        result = 31 * result + sign.hashCode()
        result = 31 * result + fraction.hashCode()
        return result
    }

    companion object {
        fun fromNumber(number: Number): NormalizedFloat? {
            val dbl = number.toDouble()

            if (dbl == 0.0) return ZERO
            if (dbl.isNaN()) return null
            if (dbl.isInfinite()) return null

            val sign = if (dbl.sign < 0) "-" else ""

            val (significandStr, fractionStr, exponentString) =
                "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                    .toRegex()
                    .find(dbl.absoluteValue.toString().lowercase())
                    ?.destructured
                    ?: error("Wrong number: $number")

            return if (exponentString.isNotEmpty()) { // scientific notation
                require(significandStr.length == 1)
                NormalizedFloat(significandStr.toInt(), fractionStr, exponentString.toInt(), sign)
            } else { // decimal number
                when {
                    dbl.absoluteValue < 1.0 -> {
                        val significantDigitPos = fractionStr.indexOfFirst { it != '0' }
                        val significand = fractionStr[significantDigitPos]
                        val fraction = fractionStr.drop(significantDigitPos + 1)
                        val exponent = -significantDigitPos - 1

                        NormalizedFloat(significand.digitToInt(), fraction, exponent, sign)
                    }

                    dbl.absoluteValue >= 1.0 -> {
                        val (significand, fractionPartStart) = significandStr.take(1).toInt() to significandStr.drop(1)
                        val fraction = fractionPartStart + fractionStr
                        val exponent = fractionPartStart.length

                        NormalizedFloat(significand, fraction, exponent, sign)
                    }

                    else -> error("Unexpected number: $number")
                }
            }
        }

        internal fun fromScientific(i: Int, fraction: String, exponent: Int, sign: String = ""): NormalizedFloat {
            return NormalizedFloat(i, fraction, exponent, sign)
        }

        val ZERO = NormalizedFloat(0, "0", 0, "")
    }
}
