/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.*

// TODO: should not be data class - it may break invariants
internal data class NumberInfo(
    val number: Double,
    val negative: Boolean,
    val fractionalPart: Double,
    val integerString: String,

    // bad property - initially not null only for very big numbers like 1.0E55.
    // For normal numbers (e.g., 1.234E-5, 1.234E+5) it will be null.
    // This is needed for of toExponential() and buildExponentString() functions.
    // Should be consistent and always present.
    val exponent: Int? = null,
    val precision:  Int? = null,
) {
    val isIntegerZero: Boolean = integerString == "0"
    val integerPart: Double = integerString.toDouble()
    val integerLength = integerString.length

    val fractionLeadingZeros = MAX_DECIMALS - length(fractionalPart)
    val fractionString: String
        get() {
            val fracPartStr = fractionalPart.toString()

            val frapPartCleanStr = fracPartStr.filter { it != '.' }
            val expIndex = frapPartCleanStr.indexOfFirst { it == 'E' }
            val fracPartFinalStr = if (expIndex != -1) {
                frapPartCleanStr.substring(0 until expIndex)
            } else {
                frapPartCleanStr
            }.trimEnd('0')

            return "0".repeat(fractionLeadingZeros) + fracPartFinalStr
        }

    fun addExp(exp: Int): NumberInfo {
        val newExp = exponent?.plus(exp) ?: exp
        return copy(exponent = newExp)
    }

    fun roundToPrecision(precision: Int = 0): NumberInfo {
        val exp = exponent ?: 0
        val totalPrecision = precision + exp

        var newFractionalPart: Double // TODO: likely wont overflow, but better to use Double
        var newIntegerPart: Double

        if (totalPrecision < 0) {
            newFractionalPart = 0.0
            val intShift = totalPrecision.absoluteValue
            newIntegerPart = if (integerLength <= intShift) {
                0.0
            } else {
                integerPart / 10.0.pow(intShift) * 10.0.pow(intShift)
            }
        } else {
            val precisionExp = MAX_DECIMAL_VALUE / 10.0.pow(totalPrecision).toLong()
            newFractionalPart = if (precisionExp == 0.0) {
                fractionalPart
            } else {
                ((fractionalPart / precisionExp).roundToLong() * precisionExp).toDouble()
            }
            newIntegerPart = integerPart
            if (newFractionalPart == MAX_DECIMAL_VALUE) {
                newFractionalPart = 0.0
                ++newIntegerPart
            }
        }

        val num = newIntegerPart + newFractionalPart.toDouble() / MAX_DECIMAL_VALUE

        return createNumberInfo(num)
    }

    companion object {
        val ZERO = NumberInfo(0.0, false, 0.0, integerString = "0")
        /**
         * max fraction length we can format (as any other format library does)
         */
        internal const val MAX_DECIMALS = 22
        internal val MAX_DECIMAL_VALUE = 10.0.pow(MAX_DECIMALS)

        internal fun createNumberInfo(num: Number): NumberInfo {
            val value = num.toDouble()
            // frac: "123", exp: 8, double: 0.00000123
            //   -> long: 000_001_230_000_000_000 (extended to max decimal digits)
            val encodeFraction = { frac: String, exp: Int ->
                var fraction = frac
                // cutting the fraction if it longer than max decimal digits
                if (exp > MAX_DECIMALS) {
                    fraction = frac.substring(0 until (frac.length - (exp - MAX_DECIMALS)))
                }
                fraction.toDouble() * 10.0.pow((MAX_DECIMALS - exp).coerceAtLeast(0))
            }

            val (intStr, fracStr, exponentString) =
                "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                    .toRegex()
                    .find(num.toDouble().absoluteValue.toString().lowercase())
                    ?.destructured
                    ?: error("Wrong number: $num")

            val exponent: Int = exponentString.toIntOrNull() ?: 0

            // number = 1.23456E+55
            if (exponent.absoluteValue >= MAX_DECIMALS) {
                return NumberInfo(
                    number = value.absoluteValue,
                    negative = value < 0,
                    // "1" -> 1
                    integerString = intStr,
                    // fraction part ignored intentionally
                    fractionalPart = 0.0,
                    // 55
                    exponent = exponent,
                )
            }

            check(exponent < MAX_DECIMALS)
            // number = 1.23E-4. double: 0.000123
            if (exponent < 0) {
                return NumberInfo(
                    number = value.absoluteValue,
                    negative = value < 0,
                    integerString = "0",
                    // "1" + "23" -> 000_123_000_000_000_000L
                    fractionalPart = encodeFraction(intStr + fracStr, exponent.absoluteValue + fracStr.length),
                )
            }

            check(exponent in 0..MAX_DECIMALS)
            // number = 1.234E+5, double: 123400.0
            if (exponent >= fracStr.length) {
                // "1" + "234" + "00" -> 123400
                val actualIntStr = intStr + fracStr + "0".repeat(exponent - fracStr.length)
                return NumberInfo(
                    number = value.absoluteValue,
                    negative = value < 0,
                    integerString = actualIntStr,
                    fractionalPart = 0.0,
                )
            }

            check(exponent >= 0 && exponent < fracStr.length)
            // number = 1.234567E+3, double: 1234.567
            val actualIntStr = intStr + fracStr.substring(0 until exponent)
            return NumberInfo(
                number = value.absoluteValue,
                negative = value < 0,
                // "1" + "[234]567" -> 1234
                integerString = actualIntStr,
                // "234[567]" -> 567_000_000_000_000_000
                fractionalPart = fracStr.substring(exponent).run { encodeFraction(this, this.length) },
            )
        }

        private fun length(v: Number): Int {
            // log10 doesn't work for values 10^17 + 1, returning 17.0 instead of 17.00001

            if (v.toLong() == 0L) {
                return 1
            }

            //var len = 0
            //var rem = v.toLong()
            //while (rem > 0) {
            //    len++
            //    rem /= 10
            //}
            //val longLen = len

            val doubleLen = floor(log10(v.toDouble())).toInt() + 1

            //require(longLen == doubleLen) {
            //    "Length mismatch: longLen=$longLen, doubleLen=$doubleLen"
            //}
            return doubleLen
        }
    }
}