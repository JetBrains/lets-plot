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
    exp: Int,
    val sign: String = ""
) {
    val fraction: String = fraction.trimEnd('0').takeIf { it.isNotEmpty() } ?: "0" // never empty
    val exp: Int = exp.takeIf { significand != 0 || this.fraction != "0" } ?: 0

    init {
        if (this.significand == 0) require(this.fraction == "0") { "i should be in 0..9, but was ${this.significand}" }
        if (this.significand != 0) require(this.significand in 1..9) { "i should be in 0..9, but was ${this.significand}" }
        require(this.fraction == "0" || this.fraction.last() != '0') { "fraction should not end with '0'" }
    }

    val decimalPart: String
        get() = when {
            exp == 0 -> fraction
            exp < 0 -> "0".repeat(-exp - 1) + significand.digitToChar() + (fraction.takeIf { it != "0" } ?: "")
            exp > 0 -> when {
                fraction == "0" -> "0"
                else -> fraction.drop(exp).takeIf(String::isNotEmpty) ?: "0"
            }

            else -> error("Unexpected state: $exp")
        }

    val wholePart: String
        get() = when {
            exp == 0 -> significand.toString()
            exp < 0 -> "0"
            exp > 0 -> significand.digitToChar() + fraction.take(exp) + "0".repeat((exp - fraction.length).coerceAtLeast(0))
            else -> error("Unexpected state: $exp")
        }

    val asDecimal: Decimal
        get() = Decimal.fromFloating(this)

    fun round(precision: Int): NormalizedFloat {
        if (precision < 0) {
            error("Precision should be non-negative, but was $precision")
        }

        if (precision > fraction.length) {
            return this
        }

        val (roundedFraction, carry) = Arithmetic.round(fraction, precision)
        return if (carry) {
            if (significand == 9) {
                NormalizedFloat(1, "0$roundedFraction", exp + 1, sign)
            } else {
                NormalizedFloat(significand + 1, roundedFraction, exp, sign)
            }
        } else {
            NormalizedFloat(significand, roundedFraction, exp, sign)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NormalizedFloat

        if (significand != other.significand) return false
        if (fraction != other.fraction) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = significand
        result = 31 * result + fraction.hashCode()
        result = 31 * result + exp
        return result
    }

    override fun toString(): String {
        return "Floating(i=$significand, fraction='$fraction', e=$exp)"
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
                        val exp = -significantDigitPos - 1

                        NormalizedFloat(significand.digitToInt(), fraction, exp, sign)
                    }

                    dbl.absoluteValue >= 1.0 -> {
                        val (significand, fractionPartStart) = significandStr.take(1).toInt() to significandStr.drop(1)
                        val fraction = fractionPartStart + fractionStr
                        val exp = fractionPartStart.length

                        NormalizedFloat(significand, fraction, exp, sign)
                    }

                    else -> error("Unexpected number: $number")
                }
            }
        }

        internal fun fromDecimal(number: Decimal): NormalizedFloat {
            if (number.wholePart == "0") {
                val significandDigitPos = number.decimalPart.indexOfFirst { it != '0' }
                if (significandDigitPos == -1) {
                    return ZERO
                }

                val i = number.decimalPart[significandDigitPos].digitToInt()
                val e = -(significandDigitPos + 1)
                val fracPart = number.decimalPart.drop(significandDigitPos + 1)
                return NormalizedFloat(i, fracPart, e, number.sign)
            } else {
                val i = number.wholePart[0].digitToInt()
                val e = number.wholePart.length - 1
                val fracPart = number.wholePart.drop(1) + number.decimalPart
                return NormalizedFloat(i, fracPart, e, number.sign)
            }
        }

        internal fun fromScientific(i: Int, fraction: String, exp: Int, sign: String = ""): NormalizedFloat {
            return NormalizedFloat(i, fraction, exp, sign)
        }

        val ZERO = NormalizedFloat(0, "0", 0, "")
    }

}
