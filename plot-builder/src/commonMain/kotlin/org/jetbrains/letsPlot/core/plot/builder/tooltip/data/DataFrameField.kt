/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipFormatting

class DataFrameField(
    private val name: String,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private var myFormatter: ((Any) -> String)? = null

    private fun initFormatter(expFormat: StringFormat.ExponentFormat): (Any) -> String {
        require(myFormatter == null)

        myFormatter = when (format) {
            null -> TooltipFormatting.createFormatter(myVariable, expFormat)
            else -> StringFormat.forOneArg(format, formatFor = name, expFormat = expFormat)::format
        }
        return myFormatter!!
    }


    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataFrame.isInitialized) { "Data context can be initialized only once" }

        myDataFrame = data
        myVariable = DataFrameUtil.findVariableOrFail(myDataFrame, name)
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {
        val formatter = myFormatter ?: initFormatter(ctx.expFormat)
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return DataPoint(
            label = name,
            value = formatter.invoke(originalValue),
            aes = null,
            isAxis = false,
            isSide = false
        )
    }

    override fun copy(): DataFrameField {
        return DataFrameField(name, format)
    }

    fun getVariableName(): String {
        return name
    }
}