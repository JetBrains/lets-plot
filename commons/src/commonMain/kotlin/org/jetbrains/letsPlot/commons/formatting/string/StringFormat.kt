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
    private val fields: List<FormatField>
) {
    fun format(value: Any): String {
        return format(listOf(value))
    }

    fun format(values: List<Any>): String {
        return buildString {
            var lastIndex = 0

            fields.forEachIndexed { i, field ->
                // text before the placeholder
                val staticPart = pattern.substring(lastIndex, field.placeholderPos.first)
                append(staticPart.replace("{{", "{").replace("}}", "}"))

                val fieldValue = when (i in values.indices) {
                    true -> field.fmt(values[i])
                    false -> field.placeholderText // fewer values than required - use placeholder text
                }
                append(fieldValue)

                lastIndex = field.placeholderPos.last + 1
            }

            // remaining text
            if (lastIndex < pattern.length) {
                val lastPart = pattern.substring(lastIndex)
                append(lastPart.replace("{{", "{").replace("}}", "}"))
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

    private data class FormatField(
        val placeholderPos: IntRange,
        val placeholderText: String, // The original "{...}" text for fallback
        val fmt: (Any) -> String
    )

    companion object {
        // Format strings contain “replacement fields” surrounded by braces {}.
        // Anything not contained in braces is considered literal text, which is copied unchanged to the output.
        // If you need to include a brace character in the literal text, it can be escaped by doubling: {{ and }}.
        //     "text" -> "text"
        //     "{{text}}" -> "{text}"
        //     "{.1f} -> 1.2
        //     "{{{.1f}}} -> {1.2}
        fun of(
            pattern: String,
            expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
            tz: TimeZone? = null,
        ): StringFormat {
            val fields = PLACEHOLDER_REGEX
                .findAll(pattern)
                .map { match ->
                    FormatField(
                        placeholderPos = match.range,
                        placeholderText = match.value,
                        fmt = createFormatter(match.value, expFormat, tz)
                    )
                }.toList()

            return StringFormat(pattern, fields)
        }

        private fun createFormatter(placeholder: String, expFormat: ExponentFormat?, tz: TimeZone?): (Any) -> String {
            if (placeholder == "{}") {
                return Any::toString
            }

            val format = placeholder.removeSurrounding("{", "}")

            if (NumberFormat.isValidPattern(format)) {
                val formatSpec = NumberFormat.parseSpec(format)

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
                        is String -> value.toDoubleOrNull()?.let(fmt::apply) ?: value
                        else -> value.toString()
                    }
                }
            } else if (isDateTimeFormat(format)) {
                val fmt = DateTimeFormatUtil.createInstantFormatter(format, tz ?: TimeZone.UTC)

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
            } else {
                // return original placeholder, syncing arguments
                return { "{$format}" }
            }
        }

        private val PLACEHOLDER_REGEX = Regex("""(?![^{]|\{\{)(\{([^{}]*)\})(?=[^}]|\}\}|$)""")
    }
}