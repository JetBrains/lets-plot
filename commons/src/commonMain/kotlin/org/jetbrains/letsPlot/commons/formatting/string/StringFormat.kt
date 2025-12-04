/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil
import org.jetbrains.letsPlot.commons.formatting.datetime.Pattern.Companion.isDateTimeFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

class StringFormat private constructor(
    private val pattern: String,
    private val expFormat: ExponentFormat?,
    private val tz: TimeZone?,
) {
    private val placeholders = PLACEHOLDER_REGEX.findAll(pattern).toList()
    private val formatters: List<(Any) -> String> = placeholders
        .map { it.groupValues[TEXT_IN_BRACES] }
        .map { pattern -> initFormatter(pattern, expFormat) }
        .toList()

    val argsNumber = formatters.size

    fun format(value: Any): String = format(listOf(value))

    fun format(values: List<Any>): String {
        val formattedParts = formatters.mapIndexed { i, fmt ->
            if (i < values.size) {
                fmt(values[i])
            } else if (i < placeholders.size) {
                // no value to format -> output the pattern itself so that the user can notice the problem
                placeholders[i].value
            } else {
                // should not be here
                "UNDEFINED"
            }
        }

        var string = pattern

        placeholders.withIndex().reversed().forEach { (i, placeholder) ->
            string = string.replaceRange(placeholder.range, formattedParts[i])
        }

        return string.replace("{{", "{").replace("}}", "}")
    }

    private fun initFormatter(pattern: String, expFormat: ExponentFormat?): ((Any) -> String) {
        when {
            pattern.isEmpty() -> return Any::toString

            NumberFormat.isValidPattern(pattern) -> {
                val formatSpec = NumberFormat.parseSpec(pattern)

                // override exponent properties if expFormat is set
                val adjustedFormatSpec = formatSpec.copy(
                    expType = expFormat?.notationType ?: formatSpec.expType,
                    minExp = expFormat?.min ?: formatSpec.minExp,
                    maxExp = expFormat?.max ?: formatSpec.maxExp
                )
                val fmt = NumberFormat(adjustedFormatSpec)

                // Try to convert value to Float.
                // If a value is not a number or a string representing a number, return the value itself as a string.
                // This way we avoid exceptions during formatting, and the user may notice the problem in the output.
                return { value: Any ->
                    when (value) {
                        is Number -> fmt.apply(value)
                        is String -> value.toFloatOrNull()?.let(fmt::apply) ?: value
                        else -> value.toString()
                    }
                }
            }

            isDateTimeFormat(pattern) -> {
                val fmt = DateTimeFormatUtil.createInstantFormatter(pattern, tz ?: TimeZone.UTC)

                // Try to convert value to epoch millis.
                // If a value is not a number or a string representing a number, return the value itself as a string.
                // This way we avoid exceptions during formatting, and the user may notice the problem in the output.
                return { value: Any ->
                    when (value) {
                        is Number -> fmt(value) // epoch millis
                        is String -> value.toLongOrNull()?.let(fmt) ?: value
                        else -> value.toString()
                    }
                }
            }

            else -> throw IllegalArgumentException("Can't detect type of pattern '$pattern' used in string pattern '${this.pattern}'")
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
        private val PLACEHOLDER_REGEX = Regex("""(?![^{]|\{\{)(\{([^{}]*)\})(?=[^}]|\}\}|$)""")
        private const val TEXT_IN_BRACES = 2

        fun valueInLinePattern() = "{}"

        // always string format
        fun forPattern(
            pattern: String,
            expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
            tz: TimeZone? = null,
        ): StringFormat {
            return StringFormat(pattern, expFormat = expFormat, tz)
        }
    }
}