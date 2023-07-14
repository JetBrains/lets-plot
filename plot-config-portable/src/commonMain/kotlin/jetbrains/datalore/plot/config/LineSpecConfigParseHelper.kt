/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import jetbrains.datalore.plot.builder.tooltip.LinesContentSpecification
import jetbrains.datalore.plot.builder.tooltip.LinesContentSpecification.Companion.LineSpec
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.tooltip.data.ConstantField
import jetbrains.datalore.plot.builder.tooltip.data.DataFrameField
import jetbrains.datalore.plot.builder.tooltip.data.MappingField
import jetbrains.datalore.plot.builder.tooltip.data.ValueSource

open class LineSpecConfigParser(
    opts: Map<String, Any>,
    private val constantsMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>,
    private val groupingVarName: String?,
    private val varBindings: List<VarBinding>,
) : OptionsAccessor(opts) {

    fun create(): LinesContentSpecification {
       return LineSpecConfigParserHelper(
           lines = if (has(Option.LinesSpec.LINES)) {
               getStringList(Option.LinesSpec.LINES)
           } else {
               null
           },
           formats = getList(Option.LinesSpec.FORMATS),
           variables = getStringList(Option.LinesSpec.VARIABLES),
           titleLine = getString(Option.LinesSpec.TITLE)
       ).parse()
    }

    internal inner class LineSpecConfigParserHelper(
        private val lines: List<String>?,
        formats: List<*>,
        variables: List<String>,
        private val titleLine: String?
    ) {
        private val myValueSources: MutableMap<Field, ValueSource> = prepareFormats(formats)
            .let { specifiedFormats ->
                val valueSources = specifiedFormats.mapValues { (field, format) ->
                    createValueSource(fieldName = field.name, isAes = field.isAes, format = format)
                }
                // the specified format for the variable should be applied also to the aes (if it doesn't have its own format())
                val aesValueSources = mutableMapOf<Field, ValueSource>()
                specifiedFormats.forEach { (field, format) ->
                    aesValueSources += getAesValueSourceForVariable(field, format, valueSources)
                }
                valueSources + aesValueSources
            }.toMutableMap()

        // Create lines from the given variable list
        private val myLinesForVariableList: List<LineSpec> = variables.map { variableName ->
            val valueSource = getValueSource(varField(variableName))
            LineSpec.defaultLineForValueSource(valueSource)
        }

        internal fun parse(): LinesContentSpecification {
            val allLines = parseLines()
            val title = titleLine?.let(::parseLine)
            return LinesContentSpecification(myValueSources.map { it.value }, allLines, title)
        }

        private fun parseLines(): List<LineSpec>? {
            val lines = lines?.map(::parseLine)
            return when {
                lines != null -> myLinesForVariableList + lines
                myLinesForVariableList.isNotEmpty() -> myLinesForVariableList
                else -> null
            }
        }

        private fun parseLine(line: String): LineSpec {
            val label = detachLabel(line)
            val valueString = line.substringAfter(LABEL_SEPARATOR)

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
            return LineSpec(
                label,
                pattern,
                fieldsInPattern
            )
        }

        private fun createValueSource(fieldName: String, isAes: Boolean, format: String? = null): ValueSource {
            return when {
                isAes && fieldName == Option.Mapping.GROUP -> {
                    requireNotNull(groupingVarName) { "Variable name for 'group' is not specified" }
                    DataFrameField(groupingVarName, format)
                }

                isAes -> {
                    val aes = Option.Mapping.toAes(fieldName)
                    when (val constant = constantsMap[aes]) {
                        null -> MappingField(aes, format = format)
                        else -> ConstantField(aes, constant, format)
                    }
                }

                else -> {
                    DataFrameField(fieldName, format)
                }
            }
        }

        private fun prepareFormats(formats: List<*>): Map<Field, String> {
            val allFormats = mutableMapOf<Field, String>()
            formats.forEach { lineFormat ->
                require(lineFormat is Map<*, *>) { "Wrong 'format' arguments" }
                require(lineFormat.has(Option.LinesSpec.Format.FIELD) && lineFormat.has(Option.LinesSpec.Format.FORMAT)) {
                    "Invalid 'format' arguments: 'field' and 'format' are expected"
                }

                val field = lineFormat[Option.LinesSpec.Format.FIELD] as String
                val format = lineFormat[Option.LinesSpec.Format.FORMAT] as String

                if (field.startsWith(AES_NAME_PREFIX)) {
                    val positionals = when (field.removePrefix(AES_NAME_PREFIX)) {
                        "X" -> org.jetbrains.letsPlot.core.plot.base.Aes.values().filter(org.jetbrains.letsPlot.core.plot.base.Aes.Companion::isPositionalX)
                        "Y" -> org.jetbrains.letsPlot.core.plot.base.Aes.values().filter(org.jetbrains.letsPlot.core.plot.base.Aes.Companion::isPositionalY)
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

        private fun detachLabel(line: String): String? {
            return if (LABEL_SEPARATOR in line) {
                line.substringBefore(LABEL_SEPARATOR).trim()
            } else {
                null
            }
        }

        private fun aesField(aesName: String) = Field(aesName, true)
        private fun varField(varName: String) = Field(varName, false)
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