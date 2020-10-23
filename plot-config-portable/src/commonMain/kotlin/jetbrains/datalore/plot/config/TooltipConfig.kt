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
        // String key has prefix '$' for aes and '@' for variables
        private val myValueSources: MutableMap<String, ValueSource> = prepareFormats(tooltipFormats)
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
            val pattern: String = SOURCE_RE_PATTERN.replace(valueString) {
                if (it.value == "\\$AES_NAME_PREFIX" || it.value == "\\$VARIABLE_NAME_PREFIX") {
                    // it is a part of the text (not of the name)
                    it.value.removePrefix("\\")
                } else {
                    fieldsInPattern += getValueSource(it.value)
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
                return Aes.values().find { it.name == aesName } ?: error("$aesName is not an aes name")
            }

            return when {
                name.startsWith(AES_NAME_PREFIX) -> {
                    val aes = getAesByName(name.removePrefix(AES_NAME_PREFIX))
                    when (val constant = constantsMap[aes]) {
                        null -> MappingValue(aes, format = format)
                        else -> ConstantValue(constant, format)
                    }
                }
                name.startsWith(VARIABLE_NAME_PREFIX) -> {
                    val varName = detachVariableName(name)
                    require(varName.isNotEmpty()) { "Variable name cannot be empty" }
                    DataFrameValue(varName, format)
                }
                else -> error("Unknown type of the field with name = \"$name\"")
            }
        }

        private fun prepareFormats(tooltipFormats: List<*>): Map<String, String> {
            val allFormats = mutableMapOf<String, String>()
            tooltipFormats.forEach { tooltipFormat ->
                require(tooltipFormat is Map<*, *>) { "Wrong tooltip 'format' arguments" }
                require(tooltipFormat.has(FIELD) && tooltipFormat.has(FORMAT)) { "Invalid 'format' arguments: 'field' and 'format' are expected" }

                val field = tooltipFormat[FIELD] as String
                val format = tooltipFormat[FORMAT] as String

                if (field.startsWith(AES_NAME_PREFIX)) {
                    val positionals = when (field.removePrefix(AES_NAME_PREFIX)) {
                        "X" -> Aes.values().filter(::isPositionalX)
                        "Y" -> Aes.values().filter(::isPositionalY)
                        else -> {
                            // it is aes name
                            allFormats[field] = format
                            emptyList()
                        }
                    }
                    positionals.forEach { aes ->
                        val aesConfigName = AES_NAME_PREFIX + aes.name
                        if (aesConfigName !in allFormats)
                            allFormats[aesConfigName] = format
                    }
                } else {
                    val varConfigName = VARIABLE_NAME_PREFIX + detachVariableName(field)
                    allFormats[varConfigName] = format
                }
            }
            return allFormats
        }

        private fun getValueSource(configName: String): ValueSource {
            val name = if (configName.startsWith(VARIABLE_NAME_PREFIX)) {
                VARIABLE_NAME_PREFIX + detachVariableName(configName)
            } else {
                configName
            }
            if (name !in myValueSources) {
                myValueSources[name] = createValueSource(name)
            }
            return myValueSources[name]!!
        }

        private fun detachVariableName(configName: String) =
            configName.removePrefix(VARIABLE_NAME_PREFIX).removeSurrounding("{", "}")

        private fun detachLabel(tooltipLine: String): String? {
            return if (LABEL_SEPARATOR in tooltipLine) {
                tooltipLine.substringBefore(LABEL_SEPARATOR).trim()
            } else {
                null
            }
        }
    }

    companion object {
        private const val AES_NAME_PREFIX = "$"
        private const val VARIABLE_NAME_PREFIX = "@"
        private const val LABEL_SEPARATOR = "|"

        // escaping ('\$', '\@') or aes name ('$aesName') or variable name ('@varName', '@{var name with spaces}')
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\$|\\@)|(\$\w+)|@(([\w$@]+)|(\{(.*?)}))""")
    }
}