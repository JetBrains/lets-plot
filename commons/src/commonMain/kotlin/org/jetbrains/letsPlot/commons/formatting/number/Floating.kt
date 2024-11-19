/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.sign

internal class Floating private constructor(// 1..9 or 0 for 0.0
    val i: Int,
    fraction: String,
    exp: Int,
    val sign: String = ""
) {
    val fraction: String = fraction.trimEnd('0').takeIf { it.isNotEmpty() } ?: "0" // never empty
    val exp: Int = exp.takeIf { i != 0 || this.fraction != "0" } ?: 0

    init {
        if (this.i == 0) require(this.fraction == "0") { "i should be in 0..9, but was ${this.i}" }
        if (this.i != 0) require(this.i in 1..9) { "i should be in 0..9, but was ${this.i}" }
        require(this.fraction == "0" || this.fraction.last() != '0') { "fraction should not end with '0'" }
    }

    val decimalPart: String
        get() = when {
            exp == 0 -> fraction
            exp < 0 -> "0".repeat(-exp - 1) + i.digitToChar() + (fraction.takeIf { it != "0" } ?: "")
            exp > 0 -> when {
                fraction == "0" -> "0"
                else -> fraction.drop(exp).takeIf(String::isNotEmpty) ?: "0"
            }

            else -> error("Unexpected state: $exp")
        }

    val wholePart: String
        get() = when {
            exp == 0 -> i.toString()
            exp < 0 -> "0"
            exp > 0 -> i.digitToChar() + fraction.take(exp) + "0".repeat((exp - fraction.length).coerceAtLeast(0))
            else -> error("Unexpected state: $exp")
        }

    val asDecimal: Decimal
        get() = Decimal.fromFloating(this)

    fun round(precision: Int): Floating {
        if (precision < 0) {
            error("Precision should be non-negative, but was $precision")
        }

        if (precision > fraction.length) {
            return this
        }

        val (roundedFraction, carry) = Arithmetic.round(fraction, precision)
        return if (carry) {
            if (i == 9) {
                Floating(1, "0$roundedFraction", exp + 1, sign)
            } else {
                Floating(i + 1, roundedFraction, exp, sign)
            }
        } else {
            Floating(i, roundedFraction, exp, sign)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Floating

        if (i != other.i) return false
        if (fraction != other.fraction) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = i
        result = 31 * result + fraction.hashCode()
        result = 31 * result + exp
        return result
    }

    override fun toString(): String {
        return "Floating(i=$i, fraction='$fraction', e=$exp)"
    }

    companion object {
        fun fromNumber(number: Number): Floating? {
            val dbl = number.toDouble()

            if (dbl == 0.0) return ZERO
            if (dbl.isNaN()) return null
            if (dbl.isInfinite()) return null

            val sign = if (dbl.sign < 0) "-" else ""

            val (intStr, fracStr, exponentString) =
                "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                    .toRegex()
                    .find(dbl.absoluteValue.toString().lowercase())
                    ?.destructured
                    ?: error("Wrong number: $number")

            return if (exponentString.isNotEmpty()) { // scientific notation
                require(intStr.length == 1)
                Floating(intStr.toInt(), fracStr, exponentString.toInt(), sign)
            } else { // decimal number
                when {
                    dbl.absoluteValue < 1.0 -> {
                        val significantDigitPos = fracStr.indexOfFirst { it != '0' }
                        val wholePart = fracStr[significantDigitPos]
                        val decimalPart = fracStr.drop(significantDigitPos + 1)
                        val exp = -significantDigitPos - 1

                        Floating(wholePart.digitToInt(), decimalPart, exp, sign)
                    }

                    dbl.absoluteValue >= 1.0 -> {
                        val (wholePart, decimalPartStart) = intStr.take(1).toInt() to intStr.drop(1)
                        val decimalPart = decimalPartStart + fracStr
                        val exp = decimalPartStart.length

                        Floating(wholePart, decimalPart, exp, sign)
                    }

                    else -> error("Unexpected number: $number")
                }
            }
        }

        internal fun fromDecimal(number: Decimal): Floating {
            if (number.wholePart == "0") {
                val significandDigitPos = number.decimalPart.indexOfFirst { it != '0' }
                if (significandDigitPos == -1) {
                    return ZERO
                }

                val i = number.decimalPart[significandDigitPos].digitToInt()
                val e = -(significandDigitPos + 1)
                val fracPart = number.decimalPart.drop(significandDigitPos + 1)
                return Floating(i, fracPart, e, number.sign)
            } else {
                val i = number.wholePart[0].digitToInt()
                val e = number.wholePart.length - 1
                val fracPart = number.wholePart.drop(1) + number.decimalPart
                return Floating(i, fracPart, e, number.sign)
            }
        }

        internal fun fromScientific(i: Int, fraction: String, exp: Int, sign: String = ""): Floating {
            return Floating(i, fraction, exp, sign)
        }

        val ZERO = Floating(0, "0", 0, "")
    }

}
