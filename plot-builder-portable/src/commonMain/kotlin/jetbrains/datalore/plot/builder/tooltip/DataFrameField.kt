/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class DataFrameField(
    private val name: String,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private lateinit var myFormatter: (Any) -> String

    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataFrame.isInitialized) { "Data context can be initialized only once" }
        myDataFrame = data

        myVariable = DataFrameUtil.findVariableOrFail(myDataFrame, name)

        myFormatter = when (format) {
            null -> TooltipFormatting.createFormatter(myVariable)
            else -> StringFormat.forOneArg(format, formatFor = name)::format
        }
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return DataPoint(
            label = name,
            value = myFormatter(originalValue),
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

    override fun getAnnotationText(index: Int): String? {
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return myFormatter(originalValue)
    }
}