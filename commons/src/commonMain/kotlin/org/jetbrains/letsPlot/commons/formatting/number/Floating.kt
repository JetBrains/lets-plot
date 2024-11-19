/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.sign

class Floating(i: Int, fraction: String, exp: Int, sign: String = "") {
    val i: Int // 1..9 or 0 for 0.0
    val fraction: String // never empty
    val exp: Int
    val sign: String

    init {
        require(i in 0..9) { "i should be in 0..9, but was $i" }
        this.i = i
        this.fraction = fraction.takeIf { it.isNotEmpty() } ?: "0"
        this.exp = exp
        this.sign = sign
    }

    fun round(precision: Int): Floating {
        val (roundedFraction, carry) = Arithmetic.round(fraction, precision)
        return if (carry) {
            Floating(i + 1, roundedFraction, exp, sign)
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

            if (dbl.isNaN()) return null
            if (dbl.isInfinite()) return null

            val sign = if (dbl.sign < 0) "-" else ""

            val (intStr, fracStr, exponentString) =
                "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                    .toRegex()
                    .find(dbl.absoluteValue.toString().lowercase())
                    ?.destructured
                    ?: error("Wrong number: $number")

            val exponentValue = (exponentString.toIntOrNull() ?: 0)

            when {
                dbl == 0.0 -> return Floating(0, "0", 0, "")
                dbl < 1.0 -> {
                    val significantDigitPos = fracStr.indexOfFirst { it != '0' }
                    val wholePart = fracStr[significantDigitPos]
                    val decimalPart = fracStr.drop(significantDigitPos + 1)
                    val exp = exponentValue - significantDigitPos - 1

                    return Floating(wholePart.digitToInt(), decimalPart, exp, sign)
                }

                dbl >= 1.0 -> {
                    val (wholePart, decimalPartStart) = intStr.take(1).toInt() to intStr.drop(1)
                    val decimalPart = decimalPartStart + fracStr
                    val exp = exponentValue + decimalPartStart.length

                    return Floating(wholePart, decimalPart, exp, sign)
                }

                else -> error("Unexpected number: $number")
            }
        }
    }
}