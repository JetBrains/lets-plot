/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil
import org.jetbrains.letsPlot.commons.formatting.datetime.Pattern.Companion.isDateTimeFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.FormatType.*
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

class StringFormat private constructor(
    private val pattern: String,
    private val formatType: FormatType,
    expFormat: ExponentFormat?,
    private val tz: TimeZone?,
) {
    enum class FormatType {
        NUMBER_FORMAT,
        DATETIME_FORMAT,
        STRING_FORMAT
    }

    private val formatters: List<(Any) -> String>

    init {
        formatters = when (formatType) {
            NUMBER_FORMAT, DATETIME_FORMAT -> listOf(initFormatter(pattern, formatType, expFormat))
            STRING_FORMAT -> {
                BRACES_REGEX.findAll(pattern)
                    .map { it.groupValues[TEXT_IN_BRACES] }
                    .map { pattern ->
                        val formatType = detectFormatType(pattern)
                        check(formatType == NUMBER_FORMAT || formatType == DATETIME_FORMAT) {
                            "Can't detect type of pattern '$pattern' used in string pattern '${this.pattern}'"
                        }
                        initFormatter(pattern, formatType, expFormat)
                    }
                    .toList()
            }
        }
    }

    val argsNumber = formatters.size

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        check(argsNumber == values.size) {
            "Can't format values $values with pattern '$pattern'. Wrong number of arguments: expected $argsNumber instead of ${values.size}"
        }

        return when (formatType) {
            NUMBER_FORMAT, DATETIME_FORMAT -> {
                require(formatters.size == 1)
                formatters.single()(values.single())
            }

            STRING_FORMAT -> {
                var index = 0
                BRACES_REGEX.replace(pattern) {
                    val originalValue = values[index]
                    val formatter = formatters[index++]
                    formatter(originalValue)
                }
                    .replace("{{", "{")
                    .replace("}}", "}")
            }
        }
    }

    private fun initFormatter(
        formatPattern: String,
        formatType: FormatType,
        expFormat: ExponentFormat?,
    ): ((Any) -> String) {
        if (formatPattern.isEmpty()) {
            return Any::toString
        }
        when (formatType) {
            NUMBER_FORMAT -> {
                val formatSpec = NumberFormat.parseSpec(formatPattern)

                // override exponent properties if expFormat is set
                val spec = formatSpec.copy(
                    expType = expFormat?.notationType ?: formatSpec.expType,
                    minExp = expFormat?.min ?: formatSpec.minExp,
                    maxExp = expFormat?.max ?: formatSpec.maxExp
                )
                val numberFormatter = NumberFormat(spec)
                return { value: Any ->
                    when (value) {
                        is Number -> numberFormatter.apply(value)
                        is String -> value.toFloatOrNull()?.let(numberFormatter::apply) ?: value
                        else -> error("Failed to format value with type ${value::class.simpleName}. Supported types are Number and String.")
                    }
                }
            }

            DATETIME_FORMAT -> {
                return DateTimeFormatUtil.createInstantFormatter(
                    formatPattern,
                    tz ?: TimeZone.UTC,
                )
            }

            else -> {
                error("Undefined format pattern $formatPattern")
            }
        }
    }

    data class ExponentFormat(
        val notationType: ExponentNotationType,
        val min: Int? = null,
        val max: Int? = null
    ) {
        companion object {
            val DEF_EXPONENT_FORMAT = ExponentFormat(ExponentNotationType.E)
        }
    }

    companion object {
        // Format strings contain “replacement fields” surrounded by braces {}.
        // Anything not contained in braces is considered literal text, which is copied unchanged to the output.
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
            formatFor: String? = null,
            expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
            tz: TimeZone?,
        ): StringFormat {
            return create(
                pattern,
                type,
                formatFor,
                expectedArgs = 1,
                expFormat = expFormat,
                tz
            )
        }

        fun forNArgs(
            pattern: String,
            argCount: Int,
            formatFor: String? = null,
            expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
            tz: TimeZone?,
        ): StringFormat {
            return create(
                pattern,
                STRING_FORMAT,
                formatFor,
                argCount,
                expFormat = expFormat,
                tz,
            )
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
            expectedArgs: Int = -1,
            expFormat: ExponentFormat? = null,
            tz: TimeZone?,
        ): StringFormat {
            val formatType = type ?: detectFormatType(pattern)
            return StringFormat(
                pattern, formatType,
                expFormat = expFormat,
                tz
            ).also {
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