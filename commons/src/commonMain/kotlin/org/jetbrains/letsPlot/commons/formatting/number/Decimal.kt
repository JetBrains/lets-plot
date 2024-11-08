/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.math.absoluteValue
import kotlin.math.sign

internal class Decimal internal constructor(
    wholePart: String,
    decimalPart: String,
    sign: String
) {
    val wholePart: String // never empty. "0" for zero, never contains leading zeros
    val decimalPart: String // never empty. "0" for zero, never contains trailing zeros
    val sign: String // empty for positive, "-" for negative

    init {
        require(wholePart.all { it.isDigit() }) {
            "Invalid wholePart: $wholePart"
        }

        require(decimalPart.all { it.isDigit() }) {
            "Invalid decimalPart: $decimalPart"
        }

        require(sign == "" || sign == "-") {
            "Sign should be empty or '-'"
        }

        this.wholePart = wholePart.trimStart('0').takeIf { it.isNotEmpty() } ?: "0"
        this.decimalPart = decimalPart.trimEnd('0').takeIf { it.isNotEmpty() } ?: "0"
        this.sign = sign
    }

    val isWholePartZero = this.wholePart == "0"
    val isDecimalPartZero = this.decimalPart == "0"
    val isZero = isWholePartZero && isDecimalPartZero

    // Returns the whole part as a Long or null if the number is larger than Long.MAX_VALUE.
    val wholeValue: Long? by lazy { wholePart.toLongOrNull() }

    fun toDouble(): Double {
        return "$sign$wholePart.$decimalPart".toDouble()
    }

    fun toFloating(): Floating {
        if (wholePart == "0") {
            val significandDigitPos = decimalPart.indexOfFirst { it != '0' }
            if (significandDigitPos == -1) {
                return Floating(0, "0", 0)
            }

            val i = decimalPart[significandDigitPos].digitToInt()
            val e = -(significandDigitPos + 1)
            val fracPart = decimalPart.drop(significandDigitPos + 1)
            return Floating(i, fracPart, e)
        } else {
            val i = wholePart[0].digitToInt()
            val e = wholePart.length - 1
            val fracPart = wholePart.drop(1) + decimalPart.toInt()
            return Floating(i, fracPart, e)
        }
    }

    // Shift decimal point to the left (shift < 0) or to the right (shift > 0).
    fun shiftDecimalPoint(shift: Int): Decimal {
        if (shift == 0) {
            return this
        }

        if (shift > 0) {
            if (decimalPart.length <= shift) {
                val zeros = "0".repeat(shift - decimalPart.length)
                return Decimal(wholePart + decimalPart + zeros, "0", sign)
            } else {
                val newIntPart = wholePart + decimalPart.take(shift)
                val newFracPart = decimalPart.drop(shift)
                return Decimal(newIntPart, newFracPart, sign)
            }
        } else {
            if (shift.absoluteValue >= wholePart.length) {
                val zeros = "0".repeat(shift.absoluteValue - wholePart.length)
                return Decimal("0", zeros + wholePart + decimalPart, sign)
            } else {
                val newIntPart = wholePart.take(wholePart.length - shift.absoluteValue)
                val newFracPart = wholePart.takeLast(shift.absoluteValue) + decimalPart
                return Decimal(newIntPart, newFracPart, sign)
            }
        }
    }

    fun iRound(precision: Int): Decimal {
        val number = wholePart + decimalPart
        val (roundedNumber, _) = iRound(number, precision)

        val decimalPoint = wholePart.length + if (roundedNumber.length > number.length) 1 else 0
        val roundedIntPart = roundedNumber.take(decimalPoint)
        val roundedFracPart = roundedNumber.drop(decimalPoint)

        return Decimal(roundedIntPart, roundedFracPart, sign)
    }

    fun fRound(precision: Int): Decimal {
        if (decimalPart.length <= precision) {
            return this
        }

        val (roundedFracPart, carry) = round(decimalPart, precision)
        val roundedIntPart = if (carry) add(wholePart, "1").first else wholePart
        return Decimal(roundedIntPart, roundedFracPart, sign)
    }


    override fun toString(): String = "$sign$wholePart.$decimalPart"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Decimal

        if (wholePart != other.wholePart) return false
        if (decimalPart != other.decimalPart) return false
        if (sign != other.sign) return false

        return true
    }

    override fun hashCode(): Int {
        var result = wholePart.hashCode()
        result = 31 * result + decimalPart.hashCode()
        result = 31 * result + sign.hashCode()
        return result
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

        private fun carryOnRound(number: String): Boolean {
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
            val trailingPart = number.takeLast(number.length - precision)
            val significantPart = number.take(precision)

            val carry = carryOnRound(trailingPart)

            val (fRoundedRestPart, carryFromFracToInt) = when {
                significantPart.isEmpty() -> "" to carry // round to integer - no fractional part
                else -> add(significantPart, if (carry) "1" else "0")
            }

            val resultFracPart = if (carryFromFracToInt) fRoundedRestPart.drop(1) else fRoundedRestPart

            return resultFracPart to carryFromFracToInt
        }

        private fun iRound(number: String, precision: Int): Pair<String, Boolean> {
            // special cases like:
            // round(16.5, 0) => 20.0
            // Without this line, it would be 0.0
            @Suppress("NAME_SHADOWING")
            val precision = if (precision == 0) 1 else precision

            if (number.length <= precision) {
                return number to false
            }

            return when (val roundingPartLength = number.length - precision) {
                0 -> number to false
                else -> when (number[precision] >= '5') {
                    true -> {
                        val valuePart = number.take(precision) + "0".repeat(roundingPartLength) // zeroing the rounding part
                        val carryValue = "1" + "0".repeat(roundingPartLength) // carry to the value part
                        add(valuePart, carryValue).first to true
                    }
                    false -> {
                        val valuePart = number.take(precision) + "0".repeat(roundingPartLength)
                        valuePart to false
                    }
                }
            }
        }
    }
}
