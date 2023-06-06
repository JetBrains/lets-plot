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

class MappingField(
    val aes: Aes<*>,
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
                    Aes.isPositionalXY(aes) -> Aes.toAxisAes(aes, myDataAccess.isYOrientation)
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
