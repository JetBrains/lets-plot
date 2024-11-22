/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number


internal object Arithmetic {

    // Add two numbers represented as strings.
    // Returns a pair of the result and a carry flag (true if the result has an additional digit).
    fun add(a: String, b: String): Pair<String, Boolean> {
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

    fun round(number: String, precision: Int): Pair<String, Boolean> {
        val trailingPart = number.takeLast(number.length - precision)
        val significantPart = number.take(precision)

        val trailingCarryFlag = carryOnRound(trailingPart)

        val (roundedSignificantPart, significantCarryFlag) = when {
            significantPart.isEmpty() -> "" to trailingCarryFlag // round to integer - no fractional part
            else -> add(significantPart, if (trailingCarryFlag) "1" else "0")
        }

        return when (significantCarryFlag) {
            true -> roundedSignificantPart.drop(1) to true
            false -> roundedSignificantPart to false
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
}
