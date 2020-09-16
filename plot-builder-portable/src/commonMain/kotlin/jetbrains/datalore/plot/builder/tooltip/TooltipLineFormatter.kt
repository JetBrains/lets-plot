/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.numberFormat.NumberFormat

interface TooltipLineFormatter {
    fun format(value: Any): String

    companion object {
        fun createTooltipLineFormatter(pattern: String): TooltipLineFormatter {
            return if (pattern.contains("""\{(.*)}""".toRegex())) {
                LinePatternFormatter(pattern)
            } else {
                NumberValueFormatter(pattern)
            }
        }
    }
}

class NumberValueFormatter(
    pattern: String
) : TooltipLineFormatter {
    private val myNumberFormatter = try {
        NumberFormat(pattern)
    } catch (e: Exception) {
        error("Wrong number pattern: $pattern")
    }

    override fun format(value: Any): String {
        return when {
            value is Number -> myNumberFormatter.apply(value)
            value.toString().matches("-?\\d+(\\.\\d+)?".toRegex()) -> {
                val strValue = value.toString()
                myNumberFormatter.apply(strValue.toFloat())
            }
            else -> value.toString()
        }
    }
}

class LinePatternFormatter(
    private val linePattern: String
) : TooltipLineFormatter {

    init {
        val myFormatList = RE_PATTERN.findAll(linePattern).map { it.groupValues[MATCHED_INDEX] }.toList()
        myFormatList.forEach { format ->
            try {
                NumberFormat(format)
            } catch (e: Exception) {
                error("Wrong pattern: $format. Number format is supported only:")
            }
        }
    }

    override fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        val expectedCount = RE_PATTERN.findAll(linePattern).count()
        if (expectedCount != values.size) {
            return ""
        }
        var index = 0
        return RE_PATTERN.replace(linePattern) { match ->
            val replPattern = match.groupValues[MATCHED_INDEX]
            val originalValue = values[index++]
            if (replPattern.isNotEmpty()) {
                NumberValueFormatter(replPattern).format(originalValue)
            } else {
                originalValue.toString()
            }
        }.replace("{{", "{").replace("}}", "}")
    }

    companion object {
        // Format strings contain “replacement fields” surrounded by curly brackets {}.
        // Anything that is not contained in brackets is considered literal text, which is copied unchanged to the output.
        // To include a bracket character in the text - it can be escaped by doubling: {{ and }}.
        val RE_PATTERN = Regex("""(?![^{])(\{([^{}]*)})(?=[^}]|$)""")
        private const val MATCHED_INDEX = 2

        fun valueInLinePattern() = "{}"
    }
}