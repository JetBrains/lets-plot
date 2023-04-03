/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class ConstantValue(
    private val aes: Aes<*>,
    private val value: Any,
    private val format: String? = null
) : ValueSource {

    private var formattedValue: String? = null
    private var isYOrientation: Boolean? = null

    override val isOutlier: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        isYOrientation = mappedDataAccess.isYOrientation
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint {
        val presentation = formattedValue ?: initFormattedValue(ctx)
        return DataPoint(
            label = "",
            value = presentation,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    private fun initFormattedValue(ctx: PlotContext): String {
        formattedValue = format?.let {
            StringFormat.forOneArg(format).format(value)
        } ?: run {
            val tooltipAes = when {
                Aes.isPositionalXY(aes) -> Aes.toAxisAes(aes, isYOrientation!!)
                else -> aes
            }

            if (ctx.hasScale(tooltipAes) && ctx.getScale(tooltipAes).isContinuousDomain && value is Number) {
                ctx.getTooltipFormatter(tooltipAes) {
                    TooltipFormatting.createFormatter(tooltipAes, ctx)
                }.invoke(value)
            } else {
                value.toString()
            }
        }

        return formattedValue!!
    }

    override fun copy(): ConstantValue {
        return ConstantValue(
            aes,
            value,
            format
        )
    }

    override fun getAnnotationText(index: Int): String? {
        return formattedValue
    }
}