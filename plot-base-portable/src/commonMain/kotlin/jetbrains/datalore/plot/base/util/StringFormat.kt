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

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        return when (myFormatType) {
            FormatType.NUMBER_FORMAT -> values.joinToString { NumberValueFormat(pattern).format(it) }
            FormatType.PATTERN_FORMAT -> LinePatternFormat(pattern).format(values)
        }
    }

    inner class NumberValueFormat(
        pattern: String
    ) {
        private val myNumberFormatter = try {
            NumberFormat(pattern)
        } catch (e: Exception) {
            error("Wrong number pattern: $pattern")
        }

        fun format(value: Any): String {
            return when (value) {
                is Number -> myNumberFormatter.apply(value)
                is String -> {
                    val numberValue = value.toFloatOrNull()
                    if (numberValue != null) {
                        myNumberFormatter.apply(numberValue.toFloat())
                    } else {
                        value
                    }
                }
                else -> error("Wrong value to format as number: $value")
            }
        }
    }

    inner class LinePatternFormat(
        private val pattern: String
    ) {
        // Format strings contain “replacement fields” surrounded by curly brackets {}.
        // Anything that is not contained in brackets is considered literal text, which is copied unchanged to the output.
        // To include a bracket character in the text - it can be escaped by doubling: {{ and }}.
        private val myRegex = Regex("""(?![^{])(\{([^{}]*)})(?=[^}]|$)""")

        private val myNumberFormatters: ArrayList<NumberValueFormat?> = ArrayList()

        init {
            val myFormatList = myRegex.findAll(pattern).map { it.groupValues[2] }.toList()
            myFormatList.forEach { format ->
                myNumberFormatters += if (format.isNotEmpty()) {
                    NumberValueFormat(format)
                } else {
                    null
                }
            }
        }

        fun format(values: List<Any>): String {
            if (myNumberFormatters.size != values.size) {
                return ""
            }
            var index = 0
            return myRegex.replace(pattern) {
                val originalValue = values[index]
                val formatter = myNumberFormatters[index++]
                formatter?.format(originalValue) ?: originalValue.toString()
            }
                .replace("{{", "{")
                .replace("}}", "}")
        }
    }

    companion object {
        fun detectFormatType(pattern: String): FormatType {
            return when {
                NumberFormat.isNumberPattern(pattern) -> FormatType.NUMBER_FORMAT
                else -> FormatType.PATTERN_FORMAT
            }
        }

        fun valueInLinePattern() = "{}"
    }
}