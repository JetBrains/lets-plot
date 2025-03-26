/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess

class MappingField(
    val aes: Aes<*>,
    override val isSide: Boolean = false,
    override val isAxis: Boolean = false,
    private val format: String? = null,
    label: String? = null
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private var myDataLabel: String? = label

    private var myFormatter: ((Any?) -> String)? = null

    private fun initFormatter(ctx: PlotContext): (Any?) -> String {
        require(myFormatter == null)

        val mappingFormatter = format?.let {
            StringFormat.forOneArg(it, formatFor = aes.name, expFormat = ctx.expFormat)
        }

//        // in tooltip use primary aes formatter (e.g. X for X_MIN, X_MAX etc)
//        val primaryAes = aes.takeUnless { Aes.isPositionalXY(it) } ?: Aes.toAxisAes(aes, myDataAccess.isYOrientation)
//
//        val plotFormatter = ctx.getTooltipFormatter(primaryAes)
//
//        fun formatter(value: Any?): String {
//            if (value != null && mappingFormatter != null) {
//                return mappingFormatter.format(value)
//            }
//            return plotFormatter.invoke(value)
//        }
//
//        myFormatter = ::formatter
//        return myFormatter!!

        val formatter = if (mappingFormatter != null) {
            { value: Any? -> value?.let { mappingFormatter.format(value) } ?: "n/a" }
        } else {
            // in tooltip use primary aes formatter (e.g. X for X_MIN, X_MAX etc)
            val primaryAes = aes.takeUnless { Aes.isPositionalXY(it) } ?: Aes.toAxisAes(aes, false) // isYOrientation always false

            if (Aes.isPositional(primaryAes)) { // ask scale for formatter
                ctx.getTooltipFormatter(primaryAes)
            } else { // use formatter provided by dtype or geom
                val fmt = myDataAccess.defaultFormatters[primaryAes] ?: Any?::toString
                { value: Any? -> value?.let { fmt(it) } ?: "n/a" }
            }
        }

        myFormatter = formatter
        return formatter
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
        val formatter = myFormatter ?: initFormatter(ctx)

        val originalValue = myDataAccess.getOriginalValue(aes, index)
        val formattedValue = formatter.invoke(originalValue)

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
}
