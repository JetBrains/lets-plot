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

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint {
        val presentation = formattedValue ?: initFormattedValue(ctx)
        return DataPoint(
            label = myDataLabel,
            value = presentation,
            aes = null,
            isAxis = false,
            isSide = false
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

    override fun getAnnotationText(index: Int): String? {
        return formattedValue
    }
}