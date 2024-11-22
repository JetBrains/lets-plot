/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.Companion.FRACTION_DELIMITER
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.Companion.MULT_SIGN
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType

internal data class FormattedNumber(
    val integerPart: String = "",
    val fractionalPart: String = "",
    val exponentialPart: String = "",
    val expType: ExponentNotationType = ExponentNotationType.E
) {
    val integerLength = if (omitUnit()) 0 else integerPart.length
    val fractionalLength = if (fractionalPart.isEmpty()) 0 else fractionalPart.length + FRACTION_DELIMITER.length
    val exponentialLength: Int
        get() {
            val match = POWER_REGEX.find(exponentialPart) ?: return exponentialPart.length
            val matchGroups = match.groups as MatchNamedGroupCollection
            val degreeLength = matchGroups["degree"]?.value?.length ?: return exponentialPart.length
            val fullLength = 2 + degreeLength // 2 for "10" in the "10^d"
            return if (omitUnit()) fullLength else 1 + fullLength // 1 for "·" in the "·10^d"
        }
    val fullLength = integerLength + fractionalLength + exponentialLength

    override fun toString(): String {
        val fractionDelimiter = FRACTION_DELIMITER.takeIf { fractionalPart.isNotEmpty() } ?: ""
        val fullString = "$integerPart$fractionDelimiter$fractionalPart$exponentialPart"
        return if (omitUnit()) {
            fullString.replace("1$MULT_SIGN", "")
        } else {
            fullString
        }
    }

    // Number of the form 1·10^n should be transformed to 10^n if expType is POW
    private fun omitUnit(): Boolean =
        expType == ExponentNotationType.POW && integerPart == "1" && fractionalPart.isEmpty() && exponentialPart.isNotEmpty()

    companion object {
        @Suppress("RegExpRedundantEscape") // breaks tests
        private val POWER_REGEX = """^${MULT_SIGN}\\\(10\^\{(?<degree>-?\d+)\}\\\)$""".toRegex()
    }
}
