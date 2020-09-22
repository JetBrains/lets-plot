/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.util

import jetbrains.datalore.base.numberFormat.NumberFormat

class StringFormat(
    val pattern: String
) {
    enum class FormatType {
        NUMBER_FORMAT,
        PATTERN_FORMAT
    }

    private val myFormatType = detectFormatType(pattern)
    private val myNumberFormatters: ArrayList<NumberFormat?> = ArrayList()

    init {
        fun initNumberFormat(pattern: String): NumberFormat {
            try {
                return NumberFormat(pattern)
            } catch (e: Exception) {
                error("Wrong number pattern: $pattern")
            }
        }

        when (myFormatType) {
            FormatType.NUMBER_FORMAT -> myNumberFormatters += initNumberFormat(pattern)
            else -> {
                val myFormatList = RE_PATTERN.findAll(pattern).map { it.groupValues[MATCH_INDEX] }.toList()
                myFormatList.forEach { format ->
                    myNumberFormatters += if (format.isNotEmpty()) {
                        initNumberFormat(format)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        if (myNumberFormatters.size != values.size) {
            return "n/a"
        }
        return when (myFormatType) {
            FormatType.NUMBER_FORMAT -> {
                require(myNumberFormatters.size == 1)
                formatValue(values.single(), myNumberFormatters.single())
            }
            FormatType.PATTERN_FORMAT -> {
                var index = 0
                RE_PATTERN.replace(pattern) {
                    val originalValue = values[index]
                    val formatter = myNumberFormatters[index++]
                    formatValue(originalValue, formatter)
                }
                    .replace("{{", "{")
                    .replace("}}", "}")
            }
        }
    }

    private fun formatValue(value: Any, numberFormatter: NumberFormat?): String {
        return when {
            numberFormatter == null -> value.toString()
            value is Number -> numberFormatter.apply(value)
            value is String -> {
                val numberValue = value.toFloatOrNull()
                if (numberValue != null) {
                    numberFormatter.apply(numberValue.toFloat())
                } else {
                    value
                }
            }
            else -> error("Wrong value to format as number: $value")
        }
    }

    companion object {
        // Format strings contain “replacement fields” surrounded by curly brackets {}.
        // Anything that is not contained in brackets is considered literal text, which is copied unchanged to the output.
        // To include a bracket character in the text - it can be escaped by doubling: {{ and }}.
        private val RE_PATTERN = Regex("""(?![^{])(\{([^{}]*)})(?=[^}]|$)""")
        const val MATCH_INDEX = 2

        fun detectFormatType(pattern: String): FormatType {
            return when {
                NumberFormat.isNumberPattern(pattern) -> FormatType.NUMBER_FORMAT
                else -> FormatType.PATTERN_FORMAT
            }
        }

        fun valueInLinePattern() = "{}"
    }
}