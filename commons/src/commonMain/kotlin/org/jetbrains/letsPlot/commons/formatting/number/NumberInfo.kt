/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.pow

internal data class NumberInfo(
    val number: Double,
    val negative: Boolean,
    val integerPart: Long,
    val fractionalPart: Long,
    val integerString: String,
    val exponent: Int? = null,
) {
    val fractionLeadingZeros = MAX_DECIMALS - length(fractionalPart)
    val integerLength = length(integerPart).also { check(it == integerString.length) { "$it != ${integerString.length}" } }
    val fractionString = "0".repeat(fractionLeadingZeros) + fractionalPart.toString().trimEnd('0')

    companion object {
        val ZERO = NumberInfo(0.0, false, 0, 0, integerString = "0")
        /**
         * max fraction length we can format (as any other format library does)
         */
        private const val MAX_DECIMALS = 18
        internal val MAX_DECIMAL_VALUE = 10.0.pow(MAX_DECIMALS).toLong()

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
                fraction.toLong() * 10.0.pow((MAX_DECIMALS - exp).coerceAtLeast(0)).toLong()
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
                    integerPart = intStr.toLong(),
                    // fraction part ignored intentionally
                    fractionalPart = 0,
                    // 55
                    exponent = exponent,
                    integerString = intStr
                )
            }

            check(exponent < MAX_DECIMALS)
            // number = 1.23E-4. double: 0.000123
            if (exponent < 0) {
                return NumberInfo(
                    number = value.absoluteValue,
                    negative = value < 0,
                    integerPart = 0,
                    // "1" + "23" -> 000_123_000_000_000_000L
                    fractionalPart = encodeFraction(intStr + fracStr, exponent.absoluteValue + fracStr.length),
                    integerString = "0"
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
                    integerPart = actualIntStr.toLong(),
                    fractionalPart = 0,
                )
            }

            check(exponent >= 0 && exponent < fracStr.length)
            // number = 1.234567E+3, double: 1234.567
            val actualIntStr = intStr + fracStr.substring(0 until exponent)
            return NumberInfo(
                number = value.absoluteValue,
                negative = value < 0,
                // "1" + "[234]567" -> 1234
                integerPart = actualIntStr.toLong(),
                integerString = actualIntStr,
                // "234[567]" -> 567_000_000_000_000_000
                fractionalPart = fracStr.substring(exponent).run { encodeFraction(this, this.length) }
            )
        }

        private fun length(v: Long): Int {
            // log10 doesn't work for values 10^17 + 1, returning 17.0 instead of 17.00001

            if (v == 0L) {
                return 1
            }

            var len = 0
            var rem = v
            while (rem > 0) {
                len++
                rem /= 10
            }

            return len
        }
    }
}