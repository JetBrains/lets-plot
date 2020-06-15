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
    label: String = "",
    format: String = ""
) : ValueSource {

    private val myFormatter = if (format.isEmpty()) null else LineFormatter(format)
    private lateinit var myDataFrame: DataFrame
    private lateinit var myVariable: DataFrame.Variable
    private val myLabel = LineFormatter.chooseLabel(dataLabel = name, userLabel = label)
    private var myIsContinuous: Boolean = false

    override fun setDataContext(dataContext: DataContext) {
        myDataFrame = dataContext.dataFrame

        val variable = myDataFrame.variables().find { it.name == name }
        requireNotNull(variable) { "Undefined variable with name '$name'" }

        myVariable = variable
        myIsContinuous = myDataFrame.isNumeric(myVariable)
    }

    override fun getDataPoint(index: Int): DataPoint? {

        val originalValue = myDataFrame[myVariable][index]
        return DataPoint(
            label = myLabel,
            value = format(originalValue, myIsContinuous),
            isContinuous = myIsContinuous,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    private fun format(originalValue: Any?, isContinuous: Boolean): String {
        // todo Need proper formatter.
        val strValue = originalValue.toString()
        return myFormatter?.format(strValue, isContinuous) ?: strValue
    }

    fun getVariableName(): String {
        return name
    }
}