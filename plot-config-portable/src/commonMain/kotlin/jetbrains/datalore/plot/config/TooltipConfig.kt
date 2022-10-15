/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalX
import jetbrains.datalore.plot.base.Aes.Companion.isPositionalY
import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.tooltip.*
import jetbrains.datalore.plot.config.Option.Mapping.GROUP
import jetbrains.datalore.plot.config.Option.TooltipFormat.FIELD
import jetbrains.datalore.plot.config.Option.TooltipFormat.FORMAT

class TooltipConfig(
    opts: Map<String, Any>,
    private val constantsMap: Map<Aes<*>, Any>,
    private val groupingVarName: String?,
    private val varBindings: List<VarBinding>
) : OptionsAccessor(opts) {

    fun createTooltips(): TooltipSpecification {
        return TooltipConfigParseHelper(
            tooltipLines = if (has(Option.Layer.TOOLTIP_LINES)) {
                getStringList(Option.Layer.TOOLTIP_LINES)
            } else {
                null
            },
            tooltipFormats = getList(Option.Layer.TOOLTIP_FORMATS),
            tooltipVariables = getStringList(Option.Layer.TOOLTIP_VARIABLES),
            tooltipTitleLine = getString(Option.Layer.TOOLTIP_TITLE)
        ).parse()
    }

    private inner class TooltipConfigParseHelper(
        private val tooltipLines: List<String>?,
        tooltipFormats: List<*>,
        tooltipVariables: List<String>,
        private val tooltipTitleLine: String?
    ) {
        private val myValueSources: MutableMap<Field, ValueSource> = prepareFormats(tooltipFormats)
            .let { specifiedFormats ->
                val valueSources = specifiedFormats.mapValues { (field, format) ->
                    createValueSource(fieldName = field.name, isAes = field.isAes, format = format)
                }
                // the specified format for the variable should be applied also to the aes (if it doesn't have its own format())
                val aesValueSources = mutableMapOf<Field, ValueSource>()
                specifiedFormats.map { (field, format) ->
                    aesValueSources += getAesValueSourceForVariable(field, format, valueSources)
                }
                valueSources + aesValueSources
            }.toMutableMap()

        // Create tooltip lines from the given variable list
        private val myLinesForVariableList: List<TooltipLine> = tooltipVariables.map { variableName ->
            val valueSource = getValueSource(varField(variableName))
            TooltipLine.defaultLineForValueSource(valueSource)
        }

        internal fun parse(): TooltipSpecification {
            val lines = tooltipLines?.map(::parseLine)
            val allTooltipLines = when {
                lines != null -> myLinesForVariableList + lines
                myLinesForVariableList.isNotEmpty() -> myLinesForVariableList
                else -> null
            }
            val tooltipTitle = tooltipTitleLine?.let(::parseLine)

            return TooltipSpecification(
                myValueSources.map { it.value },
                allTooltipLines,
                TooltipSpecification.TooltipProperties(
                    anchor = readAnchor(),
                    minWidth = readMinWidth()
                ),
                tooltipTitle
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
            return when {
                isAes && fieldName == GROUP -> {
                    requireNotNull(groupingVarName) { "Variable name for 'group' is not specified" }
                    DataFrameValue(groupingVarName, format)
                }

                isAes -> {
                    val aes = Option.Mapping.toAes(fieldName)
                    when (val constant = constantsMap[aes]) {
                        null -> MappingValue(aes, format = format)
                        else -> ConstantValue(aes, constant, format)
                    }
                }

                else -> {
                    DataFrameValue(fieldName, format)
                }
            }
        }

        private fun prepareFormats(tooltipFormats: List<*>): Map<Field, String> {
            val allFormats = mutableMapOf<Field, String>()
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

        private fun getAesValueSourceForVariable(
            field: Field,
            format: String?,
            valueSources: Map<Field, ValueSource>
        ): Map<Field, ValueSource> {
            if (field.isAes) {
                return emptyMap()
            }

            return varBindings
                .filter { it.variable.name == field.name }
                .map(VarBinding::aes).associate { aes ->
                    val aesField = aesField(aes.name)
                    if (aesField in valueSources)
                        aesField to valueSources[aesField]!!
                    else
                        aesField to createValueSource(
                            fieldName = aes.name,
                            isAes = true,
                            format = format
                        )
                }
        }

        private fun getValueSource(field: Field): ValueSource {
            if (field !in myValueSources) {
                // If format() is not specified for the variable, use the aes formatting
                val aesValueSources =
                    getAesValueSourceForVariable(field, format = null, valueSources = myValueSources)

                // Choose the specified before or use the first aes
                val specifiedBefore = (aesValueSources
                    .filter { it.key in myValueSources }
                    .takeIf { it.isNotEmpty() }
                    ?: aesValueSources)
                    .toList()
                    .minByOrNull { (aesField, _) -> aesField.name }
                    ?.second

                myValueSources[field] =
                    specifiedBefore ?: createValueSource(fieldName = field.name, isAes = field.isAes)
            }
            return myValueSources[field]!!
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

            return getValueSource(field)
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

        private fun aesField(aesName: String) = Field(aesName, true)
        private fun varField(varName: String) = Field(varName, false)

        private fun readAnchor(): TooltipAnchor? {
            if (!has(Option.Layer.TOOLTIP_ANCHOR)) {
                return null
            }

            return when (val anchor = getString(Option.Layer.TOOLTIP_ANCHOR)) {
                "top_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.LEFT)
                "top_center" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.CENTER)
                "top_right" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.TOP, TooltipAnchor.HorizontalAnchor.RIGHT)
                "middle_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.MIDDLE, TooltipAnchor.HorizontalAnchor.LEFT)
                "middle_center" -> TooltipAnchor(
                    TooltipAnchor.VerticalAnchor.MIDDLE,
                    TooltipAnchor.HorizontalAnchor.CENTER
                )

                "middle_right" -> TooltipAnchor(
                    TooltipAnchor.VerticalAnchor.MIDDLE,
                    TooltipAnchor.HorizontalAnchor.RIGHT
                )

                "bottom_left" -> TooltipAnchor(TooltipAnchor.VerticalAnchor.BOTTOM, TooltipAnchor.HorizontalAnchor.LEFT)
                "bottom_center" -> TooltipAnchor(
                    TooltipAnchor.VerticalAnchor.BOTTOM,
                    TooltipAnchor.HorizontalAnchor.CENTER
                )

                "bottom_right" -> TooltipAnchor(
                    TooltipAnchor.VerticalAnchor.BOTTOM,
                    TooltipAnchor.HorizontalAnchor.RIGHT
                )

                else -> throw IllegalArgumentException(
                    "Illegal value $anchor, ${Option.Layer.TOOLTIP_ANCHOR}, expected values are: " +
                            "'top_left'/'top_center'/'top_right'/" +
                            "'middle_left'/'middle_center'/'middle_right'/" +
                            "'bottom_left'/'bottom_center'/'bottom_right'"
                )
            }
        }

        private fun readMinWidth(): Double? {
            if (has(Option.Layer.TOOLTIP_MIN_WIDTH)) {
                return getDouble(Option.Layer.TOOLTIP_MIN_WIDTH)
            }
            return null
        }
    }

    private data class Field(val name: String, val isAes: Boolean)

    companion object {
        private const val AES_NAME_PREFIX = "^"
        private const val VARIABLE_NAME_PREFIX = "@"
        private const val LABEL_SEPARATOR = "|"

        // escaping ('\^', '\@') or aes name ('^aesName') or variable name ('@varName', '@{var name with spaces}', '@..stat_var..')
        private val SOURCE_RE_PATTERN = Regex("""(?:\\\^|\\@)|(\^\w+)|@(([\w^@]+)|(\{(.*?)\})|\.{2}\w+\.{2})""")
    }
}
