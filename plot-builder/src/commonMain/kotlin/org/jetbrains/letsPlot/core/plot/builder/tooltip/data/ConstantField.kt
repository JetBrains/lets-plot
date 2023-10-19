/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.tooltip.FormatterProvider
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint

class ConstantField(
    val aes: Aes<*>,
    private val value: Any,
    private val format: String? = null,
    label: String? = null
) : ValueSource {

    private var formattedValue: String? = null
    private var isYOrientation: Boolean? = null
    private var myDataLabel: String? = label

    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        isYOrientation = mappedDataAccess.isYOrientation
        if (myDataLabel == null) {
            myDataLabel = if (mappedDataAccess.isMapped(aes)) {
                mappedDataAccess.getMappedDataLabel(aes)
            } else {
                aes.name
            }
        }
    }

    override fun getDataPoint(index: Int, formatterProvider: FormatterProvider): DataPoint {
        val presentation = formattedValue ?: initFormattedValue(formatterProvider)
        return DataPoint(
            label = myDataLabel,
            value = presentation,
            aes = null,
            isAxis = false,
            isSide = false
        )
    }

    private fun initFormattedValue(formatterProvider: FormatterProvider): String {
        formattedValue = format?.let {
            StringFormat.forOneArg(format).format(value)
        } ?: run {
            val tooltipAes = when {
                Aes.isPositionalXY(aes) -> Aes.toAxisAes(
                    aes,
                    isYOrientation!!
                )
                else -> aes
            }

            formatterProvider.getFormatter(tooltipAes).invoke(value)

            /*
            if (ctx.hasScale(tooltipAes) && ctx.getScale(tooltipAes).isContinuousDomain && value is Number) {
                ctx.getTooltipFormatter(tooltipAes) {
                    TooltipFormatting.createFormatter(tooltipAes, ctx)
                }.invoke(value)
            } else {
                value.toString()
            }
            */
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