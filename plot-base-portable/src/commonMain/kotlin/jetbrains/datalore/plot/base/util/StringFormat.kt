/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.util

import jetbrains.datalore.base.numberFormat.NumberFormat

class StringFormat(
    private val pattern: String
) {
    enum class FormatType {
        NUMBER_FORMAT,
        STRING_FORMAT
    }

    val formatType = if (NumberFormat.isValidPattern(pattern)) {
        FormatType.NUMBER_FORMAT
    } else {
        FormatType.STRING_FORMAT
    }

    private val myNumberFormatters: List<NumberFormat?>

    init {
        fun initNumberFormat(pattern: String): NumberFormat {
            try {
                return NumberFormat(pattern)
            } catch (e: Exception) {
                error("Wrong number pattern: $pattern")
            }
        }

        myNumberFormatters = when (formatType) {
            FormatType.NUMBER_FORMAT -> listOf(initNumberFormat(pattern))
            FormatType.STRING_FORMAT -> {
                RE_PATTERN.findAll(pattern).map { it.groupValues[MATCH_INDEX] }.toList()
                    .map { format ->
                        if (format.isNotEmpty()) {
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
            error("Wrong format pattern \"$pattern\" to format values=$values")
        }
        return when (formatType) {
            FormatType.NUMBER_FORMAT -> {
                require(myNumberFormatters.size == 1)
                formatValue(values.single(), myNumberFormatters.single())
            }
            FormatType.STRING_FORMAT -> {
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
            else -> error("Failed to format value with type ${value::class.simpleName}. Supported types are Number and String.")
        }
    }

    companion object {
        // Format strings contain “replacement fields” surrounded by curly brackets {}.
        // Anything that is not contained in brackets is considered literal text, which is copied unchanged to the output.
        // To include a bracket character in the text - it can be escaped by doubling: {{ and }}.
        private val RE_PATTERN = Regex("""(?![^{])(\{([^{}]*)})(?=[^}]|$)""")
        const val MATCH_INDEX = 2

        fun valueInLinePattern() = "{}"
    }
}