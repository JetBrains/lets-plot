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
        // Key is Pair: <field name> + <isAes flag>
        private val myValueSources: MutableMap<Pair<String, Boolean>, ValueSource> = prepareFormats(tooltipFormats)
            .mapValues { (field, format) ->
                createValueSource(fieldName = field.first, isAes = field.second, format = format)
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

        private fun createValueSource(fieldName: String, isAes: Boolean, format: String? = null): ValueSource {
            fun getAesByName(aesName: String): Aes<*> {
                return Aes.values().find { it.name == aesName } ?: error("$aesName is not an aes name")
            }

            return if (isAes) {
                val aes = getAesByName(fieldName)
                when (val constant = constantsMap[aes]) {
                    null -> MappingValue(aes, format = format)
                    else -> ConstantValue(constant, format)
                }
            } else {
                DataFrameValue(fieldName, format)
            }
        }

        private fun prepareFormats(tooltipFormats: List<*>): Map<Pair<String, Boolean>, String> {
            val allFormats = mutableMapOf<Pair<String, Boolean>, String>()
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
                            val aesField = aesField(field.removePrefix(AES_NAME_PREFIX))
                            allFormats[aesField] = format
                            emptyList()
                        }
                    }
                    positionals.forEach { aes ->
                        val aesField = aesField(aes.name)
                        if (aesField !in allFormats)
                            allFormats[aesField] = format
                    }
                } else {
                    val varField = varField(detachVariableName(field))
                    allFormats[varField] = format
                }
            }
            return allFormats
        }

        private fun getValueSource(fieldString: String): ValueSource {
            val field = when {
                fieldString.startsWith(AES_NAME_PREFIX) -> {
                    aesField(fieldString.removePrefix(AES_NAME_PREFIX))
                }
                fieldString.startsWith(VARIABLE_NAME_PREFIX) -> {
                    varField(detachVariableName(fieldString))
                }
                else -> error("Unknown type of the field with name = \"$fieldString\"")
            }

            if (field !in myValueSources) {
                myValueSources[field] = createValueSource(fieldName = field.first, isAes = field.second)
            }
            return myValueSources[field]!!
        }

        private fun detachVariableName(field: String) =
            field.removePrefix(VARIABLE_NAME_PREFIX).removeSurrounding("{", "}")

        private fun detachLabel(tooltipLine: String): String? {
            return if (LABEL_SEPARATOR in tooltipLine) {
                tooltipLine.substringBefore(LABEL_SEPARATOR).trim()
            } else {
                null
            }
        }

        private fun aesField(aesName: String) = Pair(aesName, true)

        private fun varField(aesName: String) = Pair(aesName, false)
    }

    companion object {
        private const val AES_NAME_PREFIX = "$"
        private const val VARIABLE_NAME_PREFIX = "@"
        private const val LABEL_SEPARATOR = "|"

        // escaping ('\$', '\@') or aes name ('$aesName') or variable name ('@varName', '@{var name with spaces}')
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\$|\\@)|(\$\w+)|@(([\w$@]+)|(\{(.*?)}))""")
    }
}