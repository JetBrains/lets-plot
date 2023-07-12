/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.interact.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.interact.TooltipLineSpec
import org.jetbrains.letsPlot.core.plot.base.interact.TooltipLineSpec.DataPoint
import jetbrains.datalore.plot.builder.tooltip.LinesContentSpecification.Companion.LineSpec

class TooltipLine(
    label: String?,
    pattern: String,
    fields: List<ValueSource>
) : LineSpec(label, pattern, fields), TooltipLineSpec {

    constructor(other: LineSpec) : this(other.label, other.pattern, other.fields.map(ValueSource::copy))

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
                value = myLineFormatter.format(dataValues.map { it.value }),
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

    companion object {
        fun defaultLineForValueSource(valueSource: ValueSource): TooltipLine = TooltipLine(
            LineSpec.defaultLineForValueSource(valueSource)
        )
    }
}