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
        return if (!has(LINES)) {
            null
        } else {
            getList(LINES).let(::parseLines)
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
            DEFAULT_LABEL -> null
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

    companion object {
        private const val DEFAULT_LABEL = "{}"
        private const val VALUE_SOURCE_PREFIX = "$"

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