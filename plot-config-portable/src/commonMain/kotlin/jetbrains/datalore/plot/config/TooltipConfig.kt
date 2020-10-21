/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalX
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalY
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.builder.tooltip.*
import jetbrains.datalore.plot.config.Option.TooltipFormat.FIELD
import jetbrains.datalore.plot.config.Option.TooltipFormat.FORMAT

class TooltipConfig(
    opts: Map<*, *>,
    private val constantsMap: Map<Aes<*>, Any>
) : OptionsAccessor(opts) {

    fun createTooltips(): TooltipSpecification {
        return TooltipConfigParseHelper(
            tooltipLines = if (has(Option.Layer.TOOLTIP_LINES)) {
                getStringList(Option.Layer.TOOLTIP_LINES)
            } else {
                null
            },
            tooltipFormats = getList(Option.Layer.TOOLTIP_FORMATS)
        ).parse()
    }

    private inner class TooltipConfigParseHelper(
        private val tooltipLines: List<String>?,
        tooltipFormats: List<*>
    ) {
        private val myValueSources = prepareFormats(tooltipFormats)
            .mapValues {
                createValueSource(it.key, it.value)
            }.toMutableMap()

        internal fun parse(): TooltipSpecification {
            val lines = tooltipLines?.map(::parseLine)
            return TooltipSpecification(
                myValueSources.map { it.value },
                lines
            )
        }

        private fun parseLine(tooltipLine: String): TooltipLine {
            val label = detachLabel(tooltipLine)
            val valueString = tooltipLine.substringAfter(LABEL_SEPARATOR)

            val fieldsInPattern = mutableListOf<ValueSource>()
            val pattern: String = SOURCE_RE_PATTERN.replace(valueString) { match ->
                if (match.value == "\\$VALUE_SOURCE_PREFIX") {
                    // it is a part of the text (not of the name)
                    VALUE_SOURCE_PREFIX
                } else {
                    fieldsInPattern += getValueSource(match.value)
                    StringFormat.valueInLinePattern()
                }
            }
            return TooltipLine(
                label,
                pattern,
                fieldsInPattern
            )
        }

        private fun createValueSource(name: String, format: String? = null): ValueSource {
            fun getAesByName(aesName: String): Aes<*> {
                return Aes.values().find { it.name == aesName } ?: error("$aesName is not aes name")
            }

            return when {
                name.startsWith("var@") -> {
                    val varName = name.removePrefix("var@")
                    if (varName.isEmpty()) error("Variable name cannot be empty")
                    DataFrameValue(varName, format)
                }
                else -> {
                    val aes = getAesByName(name)
                    when (val constant = constantsMap[aes]) {
                        null -> MappingValue(aes, format = format)
                        else -> ConstantValue(constant, format)
                    }
                }
            }
        }

        private fun prepareFormats(tooltipFormats: List<*>): Map<String, String> {
            val allFormats = mutableMapOf<String, String>()
            tooltipFormats.forEach { tooltipFormat ->
                require(tooltipFormat is Map<*, *>) { "Wrong tooltip 'format' arguments" }
                require(tooltipFormat.has(FIELD) && tooltipFormat.has(FORMAT)) { "Invalid 'format' arguments: 'field' and 'format' are expected" }

                val configName = tooltipFormat[FIELD] as String
                val configFormat = tooltipFormat[FORMAT] as String

                if (configName.startsWith("$")) {
                    val positionals = when (configName.removePrefix("$")) {
                        "X" -> Aes.values().filter(::isPositionalX)
                        "Y" -> Aes.values().filter(::isPositionalY)
                        else -> error("X or Y is expected before '$' as positional aes")
                    }
                    positionals.forEach { aes ->
                        if (!allFormats.containsKey(aes.name))
                            allFormats[aes.name] = configFormat
                    }
                } else {
                    allFormats[configName] = configFormat
                }
            }
            return allFormats
        }

        private fun getValueSource(configName: String): ValueSource {
            val name = configName
                .removePrefix(VALUE_SOURCE_PREFIX)
                .removeSurrounding("{", "}")
            if (name !in myValueSources) {
                myValueSources[name] = createValueSource(name)
            }
            return myValueSources[name]!!
        }

        private fun detachLabel(tooltipLine: String): String? {
            return if (LABEL_SEPARATOR in tooltipLine) {
                tooltipLine.substringBefore(LABEL_SEPARATOR).trim()
            } else {
                null
            }
        }
    }

    companion object {
        private const val VALUE_SOURCE_PREFIX = "$"
        private const val LABEL_SEPARATOR = "|"

        // \$ (dollar escaping) or $name or ${name with spaces}
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\$)|\$(((\w*@)?([\w$]*[^\s\W]+\$?))|(\{(.*?)}))""")
    }
}