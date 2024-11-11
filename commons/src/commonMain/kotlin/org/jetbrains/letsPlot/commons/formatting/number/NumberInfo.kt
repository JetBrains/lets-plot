/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

// TODO: should not be data class - it may break invariants
internal class NumberInfo(
    val decimal: Decimal,
) {
    val negative: Boolean = decimal.isNegative
    val number: Double = decimal.toDouble()
    val integerString: String = decimal.wholePart
    val isIntegerZero: Boolean = decimal.isWholePartZero
    val isFractionZero: Boolean = decimal.isDecimalPartZero
    val integerPart: Double = integerString.toDouble()
    val integerLength = integerString.length

    val isZero: Boolean = decimal.isWholePartZero && decimal.isDecimalPartZero

    val fractionLeadingZeros = decimal.decimalPart.indexOfFirst { it != '0' }
    val fractionString: String
        get() {
            return decimal.decimalPart
        }

    fun fRound(precision: Int = 0): NumberInfo {
        val rounded = decimal.fRound(precision)
        return NumberInfo(decimal = rounded)
    }

    fun iRound(precision: Int = 0): NumberInfo {
        val rounded = decimal.iRound(precision)
        return NumberInfo(decimal = rounded)
    }

    fun shiftDecimalPoint(i: Int): NumberInfo {
        return NumberInfo(decimal.shiftDecimalPoint(i))
    }

    // 123.456 -> 1.23456E+2
    fun normalize(): NumberInfo {
        return shiftDecimalPoint(-decimal.toFloating().e)
    }

    companion object {
        val ZERO = NumberInfo(Decimal.fromNumber(0.0))
        internal fun createNumberInfo(num: Number): NumberInfo {
            return NumberInfo(Decimal.fromNumber(num))
        }
    }
}
