/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.stringFormat

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
                BRACES_REGEX.findAll(pattern)
                    .map { it.groupValues[TEXT_IN_BRACES] }
                    .map { format ->
                        if (format.isNotEmpty()) {
                            initNumberFormat(format)
                        } else {
                            null
                        }
                    }
                    .toList()
            }
        }
    }

    val argsNumber = myNumberFormatters.size

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        if (argsNumber != values.size) {
            error("Can't format values $values with pattern \"$pattern\"). Wrong number of arguments: expected $argsNumber instead of ${values.size}")
        }
        return when (formatType) {
            FormatType.NUMBER_FORMAT -> {
                require(myNumberFormatters.size == 1)
                formatValue(values.single(), myNumberFormatters.single())
            }
            FormatType.STRING_FORMAT -> {
                var index = 0
                BRACES_REGEX.replace(pattern) {
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
            value is String -> value.toFloatOrNull()?.let(numberFormatter::apply) ?: value
            else -> error("Failed to format value with type ${value::class.simpleName}. Supported types are Number and String.")
        }
    }

    companion object {
        // Format strings contain “replacement fields” surrounded by braces {}.
        // Anything that is not contained in braces is considered literal text, which is copied unchanged to the output.
        // If you need to include a brace character in the literal text, it can be escaped by doubling: {{ and }}.
        //     "text" -> "text"
        //     "{{text}}" -> "{text}"
        //     "{.1f} -> 1.2
        //     "{{{.1f}}} -> {1.2}
        private val BRACES_REGEX = Regex("""(?![^{]|\{\{)(\{([^{}]*)})(?=[^}]|}}|$)""")
        const val TEXT_IN_BRACES = 2

        fun valueInLinePattern() = "{}"
    }
}