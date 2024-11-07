/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.sign

internal class Decimal internal constructor(
    intPartRepr: String, // never empty. "0" for zero, Inf, NaN
    fracPartRepr: String, // never empty. "0" for zero, undefined for Inf, NaN
    signRepr: String // empty for positive, "-" for negative
) {
    val intPartRepr: String // never empty. "0" for zero, Inf, NaN
    val fracPartRepr: String // never empty. "0" for zero, undefined for Inf, NaN
    val signRepr: String // empty for positive, "-" for negative

    val isFractionalPartZero: Boolean
        get() = fracPartRepr.all { it == '0' }

    val isIntegerPartZero: Boolean get() = intPartRepr.all { it == '0' }

    init {
        require(intPartRepr == "Infinity" || intPartRepr == "NaN" || intPartRepr.all { it.isDigit() }) {
            "Invalid intPartRepr: $intPartRepr"
        }

        require(fracPartRepr.all { it.isDigit() }) {
            "Invalid fracPartRepr: $fracPartRepr"
        }

        require(signRepr == "" || signRepr == "-") {
            "Sign should be empty or '-'"
        }

        this.intPartRepr = intPartRepr.trimStart('0').takeIf { it.isNotEmpty() } ?: "0"
        this.fracPartRepr = fracPartRepr.trimEnd('0').takeIf { it.isNotEmpty() } ?: "0"
        this.signRepr = signRepr
    }

    fun toFloating(): Floating {
        if (intPartRepr == "Infinity" || intPartRepr == "NaN") {
            error("Can't convert $intPartRepr to Floating")
        }

        if (intPartRepr == "0") {
            val significandDigitPos = fracPartRepr.indexOfFirst { it != '0' }
            if (significandDigitPos == -1) {
                return Floating(0, "0", 0)
            }

            val i = fracPartRepr[significandDigitPos].digitToInt()
            val e = -(significandDigitPos + 1)
            val fracPart = fracPartRepr.drop(significandDigitPos + 1)
            return Floating(i, fracPart, e)
        } else {
            val i = intPartRepr[0].digitToInt()
            val e = intPartRepr.length - 1
            val fracPart = intPartRepr.drop(1) + fracPartRepr.toInt()
            return Floating(i, fracPart, e)
        }
    }

    // Shift decimal point to the left (shift < 0) or to the right (shift > 0).
    fun shiftDecimalPoint(shift: Int): Decimal {
        if (shift == 0) {
            return this
        }

        if (intPartRepr == "Infinity" || intPartRepr == "NaN") {
            return this
        }

        if (shift > 0) {
            if (fracPartRepr.length <= shift) {
                val zeros = "0".repeat(shift - fracPartRepr.length)
                return Decimal(intPartRepr + fracPartRepr + zeros, "0", signRepr)
            } else {
                val newIntPart = intPartRepr + fracPartRepr.take(shift)
                val newFracPart = fracPartRepr.drop(shift)
                return Decimal(newIntPart, newFracPart, signRepr)
            }
        } else {
            if (shift.absoluteValue >= intPartRepr.length) {
                val zeros = "0".repeat(shift.absoluteValue - intPartRepr.length)
                return Decimal("0", zeros + intPartRepr + fracPartRepr, signRepr)
            } else {
                val newIntPart = intPartRepr.take(intPartRepr.length - shift.absoluteValue)
                val newFracPart = intPartRepr.takeLast(shift.absoluteValue) + fracPartRepr
                return Decimal(newIntPart, newFracPart, signRepr)
            }
        }
    }

    fun round(precision: Int): Decimal {
        if (intPartRepr == "Infinity" || intPartRepr == "NaN") {
            return this
        }

        if (precision >= 0) {
            if (fracPartRepr.length <= precision) {
                return this
            }

            val (roundedFracPart, carry) = round(fracPartRepr, precision)
            val roundedIntPart = if (carry) add(intPartRepr, "1").first else intPartRepr
            return Decimal(roundedIntPart, roundedFracPart, signRepr)
        } else {
            val whole = intPartRepr + fracPartRepr
            val (roundedWhole, carry) = round(whole, precision.absoluteValue + fracPartRepr.length)

            val toTake = intPartRepr.length - precision.absoluteValue + if (carry) 1 else 0
            val roundedIntPart = roundedWhole.take(toTake) + "0".repeat(precision.absoluteValue)

            return Decimal(roundedIntPart, "0", signRepr)
        }
    }


    override fun toString(): String = "$signRepr$intPartRepr.$fracPartRepr"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Decimal

        if (intPartRepr != other.intPartRepr) return false
        if (fracPartRepr != other.fracPartRepr) return false
        if (signRepr != other.signRepr) return false

        return true
    }

    override fun hashCode(): Int {
        var result = intPartRepr.hashCode()
        result = 31 * result + fracPartRepr.hashCode()
        result = 31 * result + signRepr.hashCode()
        return result
    }



    fun toDouble(): Double {
        if (intPartRepr == "Infinity") {
            return Double.POSITIVE_INFINITY
        }
        if (intPartRepr == "-Infinity") {
            return Double.NEGATIVE_INFINITY
        }
        if (intPartRepr == "NaN") {
            return Double.NaN
        }

        return "$signRepr$intPartRepr.$fracPartRepr".toDouble()
    }


    companion object {
        fun fromNumber(value: Number): Decimal {
            val dbl = value.toDouble()

            if (dbl.isNaN()) {
                return Decimal("NaN", "", "")
            }
            if (dbl.isInfinite()) {
                return Decimal("Infinity", "", if (dbl < 0) "-" else "")
            }

            val (intStr, fracStr, exponentString) =
                "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                    .toRegex()
                    .find(dbl.absoluteValue.toString().lowercase())
                    ?.destructured
                    ?: error("Wrong number: $value")

            val exp = exponentString.toIntOrNull() ?: 0
            val sign = if (dbl.sign < 0) "-" else ""

            val (intPart, fracPart) = when {
                exp == 0 -> intStr to fracStr
                exp > 0 -> {
                    if (exp < fracStr.length) {
                        val intPart = intStr + fracStr.take(exp)
                        val fracPart = fracStr.drop(exp)
                        intPart to fracPart
                    } else {
                        val intPart = intStr + fracStr + "0".repeat(exp - fracStr.length)
                        intPart to "0"
                    }
                }

                exp < 0 -> {
                    val fracPart = "0".repeat(-exp - intStr.length) + intStr + fracStr
                    "0" to fracPart
                }

                else -> error("Unexpected state. value: $exp")
            }

            return Decimal(intPart, fracPart, sign)
        }

        // Add two numbers represented as strings.
        // Returns a pair of the result and a carry flag (true if the result has an additional digit).
        private fun add(a: String, b: String): Pair<String, Boolean> {
            val maxLength = maxOf(a.length, b.length)
            val range = (0 until maxLength).reversed()

            val deltaA = a.length - maxLength
            val deltaB = b.length - maxLength

            var carry = 0
            val result = CharArray(maxLength)
            for (i in range) {
                val va = a.getOrNull(i + deltaA)?.digitToInt() ?: 0
                val vb = b.getOrNull(i + deltaB)?.digitToInt() ?: 0
                val sum = va + vb + carry
                result[i] = '0' + (sum % 10)
                carry = sum / 10
            }

            return if (carry == 0) {
                result.concatToString() to false
            } else {
                ("1" + result.concatToString()) to true
            }
        }

        private fun roundCarry(number: String): Boolean {
            when (number.length) {
                0 -> return false
                1 -> return number.single() >= '5'
                else -> {
                    if (number.first() >= '5') return true
                    if (number.first() == '5' && number.asSequence().drop(1).any { it > '0' }) return true
                    return false
                }
            }
        }

        private fun round(number: String, precision: Int): Pair<String, Boolean> {
            val fDropPart = number.takeLast(number.length - precision)
            val fRestPart = number.take(precision)

            val carryToRestPart = roundCarry(fDropPart)

            val (fRoundedRestPart, carryFromFracToInt) = when {
                fRestPart.isEmpty() -> "" to carryToRestPart // round to integer - no fractional part
                else -> add(fRestPart, if (carryToRestPart) "1" else "0")
            }

            val resultFracPart = if (carryFromFracToInt) fRoundedRestPart.drop(1) else fRoundedRestPart

            return resultFracPart to carryFromFracToInt
        }

    }
}
