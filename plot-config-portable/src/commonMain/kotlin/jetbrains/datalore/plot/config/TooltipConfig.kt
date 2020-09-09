/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalX
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalY
import jetbrains.datalore.plot.builder.tooltip.*

class TooltipConfig(
    opts: Map<*, *>,
    private val constantsMap: Map<Aes<*>, Any>
) : OptionsAccessor(opts) {

    fun createTooltips(): TooltipLinesSpecification {
        return TooltipConfigParseHelper(
            tooltipLines = if (has(Option.Layer.TOOLTIP_LINES)) {
                getStringList(Option.Layer.TOOLTIP_LINES)
            } else {
                null
            },
            tooltipFormats = getMap(Option.Layer.TOOLTIP_FORMATS)
        ).parse()
    }

    private inner class TooltipConfigParseHelper(
        private val tooltipLines: List<String>?,
        tooltipFormats: Map<*, *>
    ) {
        private val myValueSources = prepareFormats(tooltipFormats)
            .mapValues {
                createValueSource(it.key, it.value)
            }.toMutableMap()

        internal fun parse(): TooltipLinesSpecification {
            val lines = tooltipLines?.map(::parseLine)
            return TooltipLinesSpecification(
                myValueSources.map { it.value },
                lines
            )
        }

        private fun parseLine(tooltipLine: String): TooltipLine {
            val label = detachLabel(tooltipLine)
            val valueString = tooltipLine.substringAfter(LABEL_SEPARATOR)

            val usedValueSources = mutableListOf<ValueSource>()
            val linePattern = SOURCE_RE_PATTERN.replace(valueString) { match ->
                if (match.value == "\\$VALUE_SOURCE_PREFIX") {
                    // it is a part of the text (not of the name)
                    VALUE_SOURCE_PREFIX
                } else {
                    usedValueSources += getValueSource(match.value)
                    LinePatternFormatter.valueInLinePattern()
                }
            }
            return TooltipLine(
                label,
                linePattern,
                usedValueSources
            )
        }

        private fun createValueSource(configName: String, format: String? = null): ValueSource {
            fun getAesByName(aesName: String): Aes<*> {
                return Aes.values().find { it.name == aesName } ?: error("$aesName is not aes name")
            }

            val name = if (configName.startsWith(VALUE_SOURCE_PREFIX)) {
                configName.removePrefix(VALUE_SOURCE_PREFIX).removeSurrounding("{", "}")
            } else {
                configName
            }
            return when {
                name.startsWith("var@") -> {
                    val varName = name.removePrefix("var@")
                    if (varName.isEmpty()) error("Variable name cannot be empty")
                    VariableValue(varName, format)
                }
                else -> {
                    val aes = getAesByName(name)
                    when (val constant = constantsMap[aes]) {
                        null -> MappedAes(aes, format = format)
                        else -> ConstantValue(constant, format)
                    }
                }
            }
        }

        private fun prepareFormats(tooltipFormats: Map<*, *>): Map<String, String> {
            val allFormats = mutableMapOf<String, String>()
            tooltipFormats.forEach {
                val configName = it.key as String
                if (configName.startsWith("@@")) {
                    val positionals = when (configName.removePrefix("@@")) {
                        "X" -> Aes.values().filter(::isPositionalX)
                        "Y" -> Aes.values().filter(::isPositionalY)
                        else -> error("X or Y is expected before '@@' as positional aes")
                    }
                    positionals.forEach { aes ->
                        val newConfigName = VALUE_SOURCE_PREFIX + aes.name
                        if (!allFormats.containsKey(newConfigName))
                            allFormats[newConfigName] = it.value as String
                    }

                } else {
                    allFormats[it.key as String] = it.value as String
                }
            }
            return allFormats
        }

        private fun getValueSource(configName: String): ValueSource {
            if (configName !in myValueSources) {
                val newValueSource = createValueSource(configName)
                myValueSources[configName] = newValueSource
            }
            return myValueSources[configName]!!
        }

        private fun detachLabel(tooltipLine: String): String? {
            val labelPart = tooltipLine.substringBefore(LABEL_SEPARATOR, "")
            return if (labelPart == USE_DEFAULT_LABEL) null else labelPart
        }
    }

    companion object {
        private const val VALUE_SOURCE_PREFIX = "$"
        private const val LABEL_SEPARATOR = "|"
        private const val USE_DEFAULT_LABEL = "@"

        // \$ (dollar escaping) or $name or ${name with spaces}
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\$)|\$(((\w*@)?([\w$]*[^\s\W]+\$?))|(\{(.*?)}))""")
    }
}