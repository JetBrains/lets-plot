/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.MappingField
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.ValueSource

class LinePattern(
    private val label: String?,
    private val pattern: String,
    val fields: List<ValueSource>
) : LineSpec {

    constructor(other: LinePattern) : this(other.label, other.pattern, other.fields.map(ValueSource::copy))

    private val myLineFormatter = StringFormat.forNArgs(pattern, fields.size, "fields")

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        fields.forEach { it.initDataContext(data, mappedDataAccess) }
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {
        val dataValues = fields.map { dataValue ->
            dataValue.getDataPoint(index, ctx) ?: return null
        }
        return if (dataValues.size == 1) {
            val dataValue = dataValues.single()
            DataPoint(
                label = chooseLabel(dataValue.label),
                value = myLineFormatter.format(dataValue.value),
                aes = dataValue.aes,
                isAxis = dataValue.isAxis,
                isSide = dataValue.isSide
            )
        } else {
            DataPoint(
                label = chooseLabel(dataValues.joinToString(", ") { it.label ?: "" }),
                value = myLineFormatter.format(dataValues.map(DataPoint::value)),
                aes = null,
                isAxis = false,
                isSide = false
            )
        }
    }

    private fun chooseLabel(dataLabel: String?): String? {
        return when (label) {
            DEFAULT_LABEL_SPECIFIER -> dataLabel    // use default label (from data)
            else -> label                     // use the given label (can be null)
        }
    }

    override fun getAnnotationText(index: Int): String? {
        val dataValues = fields.map { dataValue ->
            dataValue.getAnnotationText(index) ?: return null
        }
        return myLineFormatter.format(dataValues.map { it })
    }

    companion object {
        fun defaultLineForValueSource(valueSource: ValueSource): LinePattern = LinePattern(
            label = DEFAULT_LABEL_SPECIFIER,
            pattern = StringFormat.valueInLinePattern(),
            fields = listOf(valueSource)
        )
        private const val DEFAULT_LABEL_SPECIFIER = "@"


        fun prepareMappedLines(
            linePatterns: List<LinePattern>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): List<LinePattern> {
            val mappedLines = linePatterns.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingField>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedLines.forEach { it.initDataContext(dataFrame, dataAccess) }

            return mappedLines
        }
    }
}