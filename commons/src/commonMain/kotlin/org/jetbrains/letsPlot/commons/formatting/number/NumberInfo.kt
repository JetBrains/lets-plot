/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

// TODO: should not be data class - it may break invariants
internal data class NumberInfo(
    val decimal: Decimal,
    val negative: Boolean,
    val fractionalPart: Double,
) {
    val number: Double = decimal.toDouble()
    val integerString: String = decimal.wholePart
    val isIntegerZero: Boolean = integerString == "0"
    val isFractionZero: Boolean = decimal.isDecimalPartZero
    val integerPart: Double = integerString.toDouble()
    val integerLength = integerString.length

    val isZero: Boolean = decimal.isWholePartZero && decimal.isDecimalPartZero

    val fractionLeadingZeros = MAX_DECIMALS - length(fractionalPart)
    val fractionString: String
        get() {
            return decimal.decimalPart
        }

    fun fRound(precision: Int = 0): NumberInfo {
        val rounded = decimal.fRound(precision)
        return NumberInfo(
            decimal = rounded,
            negative = negative,
            fractionalPart = fractionalPart
        )
    }

    fun iRound(precision: Int = 0): NumberInfo {
        val rounded = decimal.iRound(precision)
        return NumberInfo(
            decimal = rounded,
            negative = negative,
            fractionalPart = fractionalPart
        )
    }

    fun shiftDecimalPoint(i: Int): NumberInfo {
        //return createNumberInfo(number * 10.0.pow(i))
        return NumberInfo(decimal.shiftDecimalPoint(i), negative, fractionalPart * 10.0.pow(i))
    }

    fun normalize(): NumberInfo {
        return shiftDecimalPoint(-decimal.toFloating().e)
    }

    companion object {
        val ZERO = NumberInfo(Decimal.fromNumber(0.0), false, 0.0)
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
                    decimal = Decimal.fromNumber(value.absoluteValue),
                    negative = value < 0,
                    // "1" -> 1
                    //integerString = intStr,
                    // fraction part ignored intentionally
                    fractionalPart = 0.0,
                    // 55
                    //exponent = exponent,
                )
            }

            check(exponent < MAX_DECIMALS)
            // number = 1.23E-4. double: 0.000123
            if (exponent < 0) {
                return NumberInfo(
                    decimal = Decimal.fromNumber(value.absoluteValue),
                    negative = value < 0,
                    //integerString = "0",
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
                    decimal = Decimal.fromNumber(value.absoluteValue),
                    negative = value < 0,
                    //integerString = actualIntStr,
                    fractionalPart = 0.0,
                )
            }

            check(exponent >= 0 && exponent < fracStr.length)
            // number = 1.234567E+3, double: 1234.567
            val actualIntStr = intStr + fracStr.substring(0 until exponent)
            return NumberInfo(
                decimal = Decimal.fromNumber(value.absoluteValue),
                negative = value < 0,
                // "1" + "[234]567" -> 1234
                //integerString = actualIntStr,
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