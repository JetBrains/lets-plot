/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class DataFrameValue(
    private val name: String,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private val myFormatter = format?.let {
        StringFormat.forOneArg(format, formatFor = name)
    }

    override val isOutlier: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataFrame.isInitialized) { "Data context can be initialized only once" }
        myDataFrame = data

        myVariable = DataFrameUtil.findVariableOrFail(myDataFrame, name)
    }

    override fun getDataPoint(index: Int, ctx: PlotContext): DataPoint? {
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return DataPoint(
            label = name,
            value = myFormatter?.format(originalValue) ?: originalValue.toString(),
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    override fun copy(): DataFrameValue {
        return DataFrameValue(name, format)
    }

    fun getVariableName(): String {
        return name
    }

    override fun getAnnotationText(index: Int): String? {
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return myFormatter?.format(originalValue) ?: originalValue.toString()
    }
}