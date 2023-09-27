/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipFormatting

class MappingField(
    val aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
    override val isSide: Boolean = false,
    override val isAxis: Boolean = false,
    private val format: String? = null,
    label: String? = null
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private var myDataLabel: String? = label
    private val myFormatter = format?.let {
        StringFormat.forOneArg(format, formatFor = aes.name)
    }

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataAccess.isInitialized) { "Data context can be initialized only once" }
        myDataAccess = mappedDataAccess

        require(myDataAccess.isMapped(aes)) { "$aes have to be mapped" }

        if (myDataLabel == null) {
            myDataLabel = when {
                isAxis -> null
                isSide -> null
                else -> myDataAccess.getMappedDataLabel(aes)
            }
        }
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint {
        val originalValue = myDataAccess.getOriginalValue(aes, index)
        val formattedValue =
            originalValue?.let {
                myFormatter?.format(it)
            } ?: run {
                val tooltipAes = when {
                    org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(aes) -> org.jetbrains.letsPlot.core.plot.base.Aes.toAxisAes(
                        aes,
                        myDataAccess.isYOrientation
                    )

                    else -> aes
                }
                ctx.getTooltipFormatter(tooltipAes) {
                    TooltipFormatting.createFormatter(tooltipAes, ctx)
                }.invoke(originalValue)
            }
        return DataPoint(
            label = myDataLabel,
            value = formattedValue,
            aes = aes,
            isAxis = isAxis,
            isSide = isSide
        )
    }

    override fun copy(): MappingField {
        return MappingField(
            aes = aes,
            isSide = isSide,
            isAxis = isAxis,
            format = format,
            label = myDataLabel
        )
    }

    fun withFlags(isSide: Boolean, isAxis: Boolean, label: String?): MappingField {
        return MappingField(
            aes = aes,
            isSide = isSide,
            isAxis = isAxis,
            format = format,
            label = label
        )
    }

    override fun getAnnotationText(index: Int): String? {
        val originalValue = myDataAccess.getOriginalValue(aes, index) ?: return null
        return myFormatter?.format(originalValue) ?: originalValue.toString()
    }
}
