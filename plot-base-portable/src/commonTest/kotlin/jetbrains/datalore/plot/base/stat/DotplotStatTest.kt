/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DotplotStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun df(dataMap: Map<DataFrame.Variable, List<Double?>>): DataFrame {
        val builder = DataFrame.Builder()
        for (key in dataMap.keys) {
            builder.putNumeric(key, dataMap.getValue(key))
        }
        return builder.build()
    }

    private fun checkStatVar(statDf: DataFrame, variable: DataFrame.Variable, expectedValues: List<Double?>) {
        assertTrue(statDf.has(variable), "Has var " + variable.name)
        assertEquals(expectedValues.size, statDf[variable].size, "Size var " + variable.name)
        for (i in expectedValues.indices)
            assertEquals(expectedValues[i], statDf[variable][i], "Get var " + variable.name)
    }

    @Test
    fun emptyDataFrame() {
        val df = df(emptyMap())
        val stat = Stats.dotplot()
        val statDf = stat.apply(df, statContext(df))

        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                checkStatVar(statDf, stat.getDefaultMapping(aes), emptyList())
            }
        }
    }

    @Test
    fun oneElementDataFrame() {
        val xValue = 3.14
        val df = df(mapOf(
            TransformVar.X to listOf(xValue)
        ))
        val stat = Stats.dotplot()
        val statDf = stat.apply(df, statContext(df))

        checkStatVar(statDf, Stats.X, listOf(xValue))
        checkStatVar(statDf, Stats.COUNT, listOf(1.0))
        checkStatVar(statDf, Stats.DENSITY, listOf(1.0))
        checkStatVar(statDf, Stats.BIN_WIDTH, listOf(1.0))
    }

    @Test
    fun oneStackDataFrame() {
        val xValue = 2.71
        val df = df(mapOf(
            TransformVar.X to listOf(xValue, xValue, xValue)
        ))
        val stat = Stats.dotplot()
        val statDf = stat.apply(df, statContext(df))

        checkStatVar(statDf, Stats.X, listOf(xValue))
        checkStatVar(statDf, Stats.COUNT, listOf(3.0))
        checkStatVar(statDf, Stats.DENSITY, listOf(1.0))
        checkStatVar(statDf, Stats.BIN_WIDTH, listOf(1.0))
    }

    @Test
    fun withNanValues() {
        val df = df(mapOf(
            TransformVar.X to listOf(0.0, 1.0, 2.0, null)
        ))
        val stat = Stats.dotplot(binWidth = 1.5)
        val statDf = stat.apply(df, statContext(df))

        checkStatVar(statDf, Stats.X, listOf(0.5, 2.0))
        checkStatVar(statDf, Stats.COUNT, listOf(2.0, 1.0))
        checkStatVar(statDf, Stats.DENSITY, listOf(2.0 / 3, 1.0 / 3))
        checkStatVar(statDf, Stats.BIN_WIDTH, listOf(1.5, 1.5))
    }
}