/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class DataFrameValue(
    private val name: String,
    format: String? = null
) : ValueSource {

    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private var myIsContinuous: Boolean = false
    private val myFormatter = format?.let {
        StringFormat(format).also {
            require(it.argsNumber == 1) { "Wrong number of arguments in pattern \'$format\' to format \'$name\'. Expected 1 argument instead of ${it.argsNumber}" }
        }
    }

    override fun setDataContext(dataContext: DataContext) {
        myDataFrame = dataContext.dataFrame

        myVariable = myDataFrame.variables().find { it.name == name } ?: error("Undefined variable with name '$name'")
        myIsContinuous = myDataFrame.isNumeric(myVariable)
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val originalValue = myDataFrame[myVariable][index].toString()
        return DataPoint(
            label = name,
            value = myFormatter?.format(originalValue) ?: originalValue,
            isContinuous = myIsContinuous,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    fun getVariableName(): String {
        return name
    }
}