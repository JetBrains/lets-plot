/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.StatContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class BaseStatTest {
    protected fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    protected fun dataFrame(dataMap: Map<DataFrame.Variable, List<Double?>>): DataFrame {
        val builder = DataFrame.Builder()
        for (key in dataMap.keys) {
            builder.putNumeric(key, dataMap.getValue(key))
        }
        return builder.build()
    }

    private fun checkStatVar(statDf: DataFrame, variable: DataFrame.Variable) {
        assertTrue(statDf.has(variable), "Has var " + variable.name)
    }

    protected fun checkStatVarValues(statDf: DataFrame, variable: DataFrame.Variable, expectedValues: List<Double?>) {
        checkStatVar(statDf, variable)
        assertEquals(expectedValues.size, statDf[variable].size, "Size var " + variable.name)
        for (i in expectedValues.indices)
            assertEquals(expectedValues[i], statDf[variable][i], "Get var " + variable.name)
    }

    protected fun checkStatVarDomain(
        statDf: DataFrame,
        variable: DataFrame.Variable,
        expectedValuesDomain: Set<Double?>
    ) {
        checkStatVar(statDf, variable)
        assertEquals(statDf.getNumeric(variable).toSet(), expectedValuesDomain, "Unique values of var " + variable.name)
    }

    protected fun checkStatVarRange(statDf: DataFrame, variable: DataFrame.Variable, expectedValuesRange: DoubleSpan) {
        checkStatVar(statDf, variable)
        val actualMinValue = statDf.getNumeric(variable).minByOrNull { it!! }!!
        assertEquals(expectedValuesRange.lowerEnd, actualMinValue, "Min value of var " + variable.name)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertEquals(expectedValuesRange.upperEnd, actualMaxValue, "Max value of var " + variable.name)
    }

    protected fun checkStatVarMaxValue(statDf: DataFrame, variable: DataFrame.Variable, expectedMaxValue: Double) {
        checkStatVar(statDf, variable)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertEquals(expectedMaxValue, actualMaxValue, "Max value of var " + variable.name)
    }

    protected fun checkStatVarMaxLimit(statDf: DataFrame, variable: DataFrame.Variable, expectedMaxLimit: Double) {
        checkStatVar(statDf, variable)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertTrue(expectedMaxLimit - actualMaxValue > 0, "Max value of var " + variable.name + " limited")
    }

    protected fun testEmptyDataFrame(stat: Stat) {
        val df = dataFrame(emptyMap())
        val statDf = stat.apply(df, statContext(df))

        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                checkStatVarValues(statDf, stat.getDefaultMapping(aes), emptyList())
            }
        }
    }
}