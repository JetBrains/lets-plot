/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormat
import org.jetbrains.letsPlot.commons.formatting.datetime.Pattern.Companion.isDateTimeFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.FormatType.*
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone

class StringFormat private constructor(
    private val pattern: String,
    private val formatType: FormatType
) {
    enum class FormatType {
        NUMBER_FORMAT,
        DATETIME_FORMAT,
        STRING_FORMAT
    }

    private val myFormatters: List<(Any) -> String>

    init {
        myFormatters = when (formatType) {
            NUMBER_FORMAT, DATETIME_FORMAT -> listOf(initFormatter(pattern, formatType))
            STRING_FORMAT -> {
                BRACES_REGEX.findAll(pattern)
                    .map { it.groupValues[TEXT_IN_BRACES] }
                    .map { format ->
                        val formatType = detectFormatType(format)
                        require(formatType == NUMBER_FORMAT || formatType == DATETIME_FORMAT) {
                            error("Can't detect type of pattern '$format' used in string pattern '$pattern'")
                        }
                        initFormatter(format, formatType)
                    }
                    .toList()
            }
        }
    }

    val argsNumber = myFormatters.size

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        if (argsNumber != values.size) {
            error("Can't format values $values with pattern '$pattern'. Wrong number of arguments: expected $argsNumber instead of ${values.size}")
        }
        return when (formatType) {
            NUMBER_FORMAT, DATETIME_FORMAT -> {
                require(myFormatters.size == 1)
                myFormatters.single()(values.single())
            }
            STRING_FORMAT -> {
                var index = 0
                BRACES_REGEX.replace(pattern) {
                    val originalValue = values[index]
                    val formatter = myFormatters[index++]
                    formatter(originalValue)
                }
                    .replace("{{", "{")
                    .replace("}}", "}")
            }
        }
    }

    private fun initFormatter(formatPattern: String, formatType: FormatType): ((Any) -> String) {
        if (formatPattern.isEmpty()) {
            return Any::toString
        }
        when (formatType) {
            NUMBER_FORMAT -> {
                val formatSpec = NumberFormat.parseSpec(formatPattern).copy(richOutput = true) // force use of superscript exponent
                val numberFormatter = NumberFormat(formatSpec)
                return { value: Any ->
                    when (value) {
                        is Number -> numberFormatter.apply(value)
                        is String -> value.toFloatOrNull()?.let(numberFormatter::apply) ?: value
                        else -> error("Failed to format value with type ${value::class.simpleName}. Supported types are Number and String.")
                    }
                }
            }
            DATETIME_FORMAT -> {
                val dateTimeFormatter = DateTimeFormat(formatPattern)
                return { value: Any ->
                    require(value is Number) {
                        error("Value '$value' to be formatted as DateTime expected to be a Number, but was ${value::class.simpleName}")
                    }
                    value.toLong()
                        .let(::Instant)
                        .let(TimeZone.UTC::toDateTime)
                        .let(dateTimeFormatter::apply)
                }
            }
            else -> {
                error("Undefined format pattern $formatPattern")
            }
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
        private val BRACES_REGEX = Regex("""(?![^{]|\{\{)(\{([^{}]*)\})(?=[^}]|\}\}|$)""")
        private const val TEXT_IN_BRACES = 2

        fun valueInLinePattern() = "{}"

        fun forOneArg(
            pattern: String,
            type: FormatType? = null,
            formatFor: String? = null
        ): StringFormat {
            return create(pattern, type, formatFor, expectedArgs = 1)
        }

        fun forNArgs(
            pattern: String,
            argCount: Int,
            formatFor: String? = null
        ): StringFormat {
            return create(pattern, STRING_FORMAT, formatFor, argCount)
        }

        private fun detectFormatType(pattern: String): FormatType {
            return when {
                NumberFormat.isValidPattern(pattern) -> NUMBER_FORMAT
                isDateTimeFormat(pattern) -> DATETIME_FORMAT
                else -> STRING_FORMAT
            }
        }

        internal fun create(
            pattern: String,
            type: FormatType? = null,
            formatFor: String? = null,
            expectedArgs: Int = -1
        ): StringFormat {
            val formatType = type ?: detectFormatType(pattern)
            return StringFormat(pattern, formatType).also {
                if (expectedArgs > 0) {
                    require(it.argsNumber == expectedArgs) {
                        @Suppress("NAME_SHADOWING")
                        val formatFor = formatFor?.let { "to format \'$formatFor\'" } ?: ""
                        "Wrong number of arguments in pattern \'$pattern\' $formatFor. " +
                                "Expected $expectedArgs ${if (expectedArgs > 1) "arguments" else "argument"} " +
                                "instead of ${it.argsNumber}"
                    }
                }
            }
        }
    }
}