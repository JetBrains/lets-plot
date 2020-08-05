/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.builder.tooltip.*
import jetbrains.datalore.plot.config.Option.LayerTooltips.LINES
import jetbrains.datalore.plot.config.Option.TooltipLine

class TooltipConfig(
    opts: Map<*, *>,
    private val constantsMap: Map<Aes<*>, Any>
) : OptionsAccessor(opts) {

    fun createTooltips(): List<TooltipLineSpecification>? {
        return when {
            has(LINES) -> getList(LINES).let(::parseLines)  // config with "tooltips" parameter
            has(Option.Layer.TOOLTIP_LINES) -> {            // config with "tooltip_lines"/"tooltip_formats" parameters
                TooltipConfigParseHelper(
                    getStringList(Option.Layer.TOOLTIP_LINES),
                    getMap(Option.Layer.TOOLTIP_FORMATS)
                ).parse()
            }
            else -> null
        }
    }

    private fun parseLines(tooltipLines: List<*>): List<TooltipLineSpecification> {
        return tooltipLines.map { tooltipLine ->
            when (tooltipLine) {
                is String -> TooltipLineSpecification.singleValueLine(
                    label = "",
                    format = "",
                    datum = createValueSource(getValueSourceName(tooltipLine))
                )
                is Map<*, *> -> parseMap(tooltipLine)
                else -> error("Error tooltip_line parsing")
            }
        }
    }

    private fun parseMap(tooltipLine: Map<*, *>): TooltipLineSpecification {
        val values: List<String> = when (val value = tooltipLine[TooltipLine.VALUE]) {
            is String -> listOf(value)
            is List<*> -> value.mapNotNull(Any?::toString)
            else -> error("Unsupported tooltip format type")
        }
        val valueSourceNames = values.map(::getValueSourceName)
        val label = when (val labelValue = tooltipLine.getString(TooltipLine.LABEL)) {
            DEFAULT_VALUE_PATTERN -> null
            else -> labelValue
        }
        val format = tooltipLine.getString(TooltipLine.FORMAT)

        return createTooltipLineSpecification(valueSourceNames, label, format)
    }

    private fun createTooltipLineSpecification(
        values: List<String>,
        label: String?,
        format: String?
    ): TooltipLineSpecification {
        return if (values.size == 1) {
            TooltipLineSpecification.singleValueLine(
                label = "",
                format = "",
                datum = createValueSource(name = values.single(), label = label, format = format)
            )
        } else {
            TooltipLineSpecification.multiValueLine(
                label = label ?: "",
                format = format ?: "",
                data = values.map { createValueSource(it) }
            )
        }
    }

    private fun createValueSource(name: String, label: String? = null, format: String? = null): ValueSource {
        fun getAesByName(aesName: String): Aes<*> {
            return Aes.values().find { it.name == aesName } ?: error("$aesName is not aes name")
        }

        return when {
            name.startsWith("text@") -> StaticValue((name.removePrefix("text@")))
            name.startsWith("var@") -> VariableValue(name.removePrefix("var@"), label, format)
            else -> {
                val aes = getAesByName(name)
                when (val constant = constantsMap[aes]) {
                    null -> MappedAes(aes, label = label, format = format)
                    else -> ConstantValue(label, constant, format)
                }
            }
        }
    }

    // Config with "tooltip_lines"/"tooltip_formats" parameters
    private inner class TooltipConfigParseHelper(
        private val tooltipLines: List<String>,
        private val tooltipFormats: Map<*, *>
    ) {
        internal fun parse(): List<TooltipLineSpecification> {
            return tooltipLines.map(::parseLine)
        }

        private fun parseLine(tooltipLine: String): TooltipLineSpecification {
            val label = detachLabel(tooltipLine)
            val valueString = tooltipLine.substringAfter(LABEL_SEPARATOR)

            val matchResult = SOURCE_RE_PATTERN.findAll(valueString).map {
                it.groupValues[MATCHED_INDEX]
            }.toList()

            var index = 0
            val linePattern = SOURCE_RE_PATTERN.replace(valueString) {
                createFormatPattern(matchResult[index++])
            }

            val valueSourceNames = if (matchResult.isNotEmpty()) {
                matchResult
            } else {
                listOf(valueString)
            }.map(::getValueSourceName)

            return createTooltipLineSpecification(
                valueSourceNames, label, linePattern
            )
        }

        private fun createFormatPattern(name: String): String {
            val format = tooltipFormats[name] as String? ?: ""
            return if (format.isNotEmpty()) {
                "{$format}"
            } else {
                DEFAULT_VALUE_PATTERN
            }
        }

        private fun detachLabel(tooltipLine: String): String? {
            val labelPart = tooltipLine.substringBefore(LABEL_SEPARATOR, "")
            return if (labelPart == USE_DEFAULT_LABEL) null else labelPart
        }
    }

    companion object {
        private const val DEFAULT_VALUE_PATTERN = "{}"
        private const val VALUE_SOURCE_PREFIX = "$"
        private const val LABEL_SEPARATOR = "|"
        private const val USE_DEFAULT_LABEL = "@"

        private val SOURCE_RE_PATTERN = Regex("""\$(((\w+@)?\w+)|(\{(\w+@)?[\w\s]+}))""")
        private const val MATCHED_INDEX = 0

        private fun getValueSourceName(value: String): String {
            return if (value.startsWith(VALUE_SOURCE_PREFIX)) {
                value.removePrefix(VALUE_SOURCE_PREFIX).removeSurrounding("{", "}")
            } else {
                // mark as the text
                "text@$value"
            }
        }
    }
}