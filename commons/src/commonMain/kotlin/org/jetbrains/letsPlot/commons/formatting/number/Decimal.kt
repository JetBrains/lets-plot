/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.Arithmetic.add
import org.jetbrains.letsPlot.commons.formatting.number.Arithmetic.round
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
    val isNegative = sign == "-"

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

    val asNormalizedFloat: NormalizedFloat by lazy { NormalizedFloat.fromDecimal(this) }

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
        val ZERO: Decimal = Decimal("0", "0", "")

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

        fun fromFloating(normalizedFloat: NormalizedFloat): Decimal {
            if (normalizedFloat == NormalizedFloat.ZERO) {
                return ZERO
            }

            return Decimal(
                wholePart = normalizedFloat.wholePart,
                decimalPart = normalizedFloat.decimalPart,
                sign = normalizedFloat.sign
            )
        }
    }
}
