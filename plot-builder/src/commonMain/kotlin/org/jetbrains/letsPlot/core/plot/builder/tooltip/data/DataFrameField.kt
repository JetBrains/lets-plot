/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.FormatterProvider
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec.DataPoint

class DataFrameField(
    private val name: String,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private val myFormatter =  format?.let {
        StringFormat.forOneArg(format, formatFor = name)
    }

    override val isSide: Boolean = false
    override val isAxis: Boolean = false

    override fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        require(!::myDataFrame.isInitialized) { "Data context can be initialized only once" }
        myDataFrame = data

        myVariable = DataFrameUtil.findVariableOrFail(myDataFrame, name)
    }

    override fun getDataPoint(index: Int, formatterProvider: FormatterProvider): DataPoint? {
        val originalValue = myDataFrame[myVariable][index] ?: return null
        return DataPoint(
            label = name,
            value = myFormatter?.format(originalValue) ?: formatterProvider.getFormatter(myVariable).invoke(originalValue),
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