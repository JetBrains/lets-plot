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

class TooltipConfig(opts: Map<*, *>) : OptionsAccessor(opts) {

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
                    datum = createValueSource(tooltipLine, label = "", format = "")
                )
                is Map<*, *> -> parseMap(tooltipLine)
                else -> error("Error tooltip_line parsing")
            }
        }
    }

    private fun parseMap(tooltipLine: Map<*, *>): TooltipLineSpecification {
        val value: List<String> = when (val value = tooltipLine[TooltipLine.VALUE]) {
            is String -> listOf(value)
            is List<*> -> value.mapNotNull(Any?::toString)
            else -> error("Unsupported tooltip format type")
        }
        val label = tooltipLine.getString(TooltipLine.LABEL) ?: ""
        val format = tooltipLine.getString(TooltipLine.FORMAT) ?: ""

        return if (value.size == 1) {
            val valueSource = createValueSource(name = value.single(), label = label, format = format)
            TooltipLineSpecification.singleValueLine(
                label = "",
                format = "",
                datum = valueSource
            )
        } else {
            val values = value.map { createValueSource(it, label = "", format = "") }
            TooltipLineSpecification.multiValueLine(
                label = label,
                format = format,
                data = values
            )
        }
    }

    private fun createValueSource(name: String, label: String, format: String): ValueSource {
        fun getAesByName(aesName: String): Aes<*> {
            return Aes.values().find { it.name == aesName } ?: error("$aesName is not aes name")
        }

        return when {
            name.startsWith("text@") -> StaticValue((name.removePrefix("text@")))
            name.startsWith("aes@") -> {
                val aes = getAesByName(name.removePrefix("aes@"))
                when {
                    format.isNotEmpty() || label.isNotEmpty() -> ConstantAes(aes, label, format)
                    else -> MappedAes(aes)
                }
            }
            else -> VariableValue(name, label, format)
        }
    }
}