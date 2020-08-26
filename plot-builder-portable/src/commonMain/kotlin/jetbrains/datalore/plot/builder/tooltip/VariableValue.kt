/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.base.interact.ValueSource.DataPoint

class VariableValue(
    private val name: String,
    private val label: String? = "",
    format: String? = null
) : ValueSource {

    private val myFormatter = LineFormatter(format)
    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private var myIsContinuous: Boolean = false

    override fun setDataContext(dataContext: DataContext) {
        myDataFrame = dataContext.dataFrame

        myVariable = myDataFrame.variables().find { it.name == name } ?: error("Undefined variable with name '$name'")
        myIsContinuous = myDataFrame.isNumeric(myVariable)
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val originalValue = myDataFrame[myVariable][index]
        return DataPoint(
            label = getLabel(),
            value = format(originalValue),
            isContinuous = myIsContinuous,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    private fun format(originalValue: Any?): String {
        // todo Need proper formatter.
        val strValue = originalValue.toString()
        return myFormatter.format(strValue, myIsContinuous)
    }

    fun getVariableName(): String {
        return name
    }

    fun getLabel(): String {
        return label ?: name
    }
}