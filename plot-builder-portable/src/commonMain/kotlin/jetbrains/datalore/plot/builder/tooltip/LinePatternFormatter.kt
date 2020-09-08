/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.numberFormat.NumberFormat

class LinePatternFormatter(
    private val myLinePattern: String
) {
    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        val expectedCount = RE_PATTERN.findAll(myLinePattern).count()
        if (expectedCount != values.size) {
            return ""
        }
        var index = 0
        return RE_PATTERN.replace(myLinePattern) { match ->
            val replPattern = match.groupValues[MATCHED_INDEX]
            val originalValue = values[index++]
            formatValue(originalValue, replPattern)
        }
    }

    private fun formatValue(originalValue: Any, replPattern: String): String {
        return if (originalValue is Number && replPattern.isNotEmpty()) {
            NumberFormat(replPattern).apply(originalValue)
        } else {
            originalValue.toString()
        }
    }

    companion object {
        private const val PREFIX_VALUE_FORMAT = "%%"
        private const val INSIDE_BRACKETS_PATTERN = """\{([^{}]*)}"""
        val RE_PATTERN = """$PREFIX_VALUE_FORMAT$INSIDE_BRACKETS_PATTERN""".toRegex()
        private const val MATCHED_INDEX = 1

        private fun formattedValueInLinePattern(format: String) = "$PREFIX_VALUE_FORMAT{$format}"

        fun valueInLinePattern() =
            formattedValueInLinePattern(
                format = ""
            )
    }
}