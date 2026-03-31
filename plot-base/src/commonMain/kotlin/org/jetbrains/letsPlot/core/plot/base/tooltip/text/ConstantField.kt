/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.text

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.PlotContext

class ConstantField(
    val aes: Aes<*>,
    private val value: Any,
    private val format: String? = null,
    label: String? = null
) : ValueSource {

    private var formattedValue: String? = null
    private var myDataLabel: String? = label

    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        if (myDataLabel == null) {
            myDataLabel = if (mappedDataAccess.isMapped(aes)) {
                mappedDataAccess.getMappedDataLabel(aes)
            } else {
                aes.name
            }
        }
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): LineSpec.DataPoint {
        val presentation = formattedValue ?: initFormattedValue(ctx)
        return LineSpec.DataPoint(
            label = myDataLabel,
            value = presentation,
            aes = null,
            isAxis = false,
            isSide = false
        )
    }

    private fun initFormattedValue(ctx: PlotContext): String {
        formattedValue = format?.let {
            FormatterUtil.byPattern(format, expFormat = ctx.expFormat, tz = ctx.tz).format(value)
        } ?: run {
            val tooltipAes = when {
                Aes.isPositionalXY(aes) -> Aes.toAxisAes(aes)
                else -> aes
            }

            if (ctx.hasScale(tooltipAes) && ctx.getScale(tooltipAes).isContinuousDomain && value is Number) {
                ctx.getTooltipFormatter(tooltipAes).invoke(value)
            } else {
                value.toString()
            }
        }

        return formattedValue!!
    }

    override fun copy(): ConstantField {
        return ConstantField(
            aes,
            value,
            format,
            myDataLabel
        )
    }

    fun withLabel(label: String? = null): ConstantField {
        return ConstantField(
            aes,
            value,
            format,
            label
        )
    }
}