/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class BaseStatTest {
    protected fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    protected fun df(dataMap: Map<DataFrame.Variable, List<Double?>>): DataFrame {
        val builder = DataFrame.Builder()
        for (key in dataMap.keys) {
            builder.putNumeric(key, dataMap.getValue(key))
        }
        return builder.build()
    }

    protected fun checkStatVar(statDf: DataFrame, variable: DataFrame.Variable, expectedValues: List<Double?>) {
        assertTrue(statDf.has(variable), "Has var " + variable.name)
        assertEquals(expectedValues.size, statDf[variable].size, "Size var " + variable.name)
        for (i in expectedValues.indices)
            assertEquals(expectedValues[i], statDf[variable][i], "Get var " + variable.name)
    }

    protected fun testEmptyDataFrame(stat: BaseStat) {
        val df = df(emptyMap())
        val statDf = stat.apply(df, statContext(df))

        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                checkStatVar(statDf, stat.getDefaultMapping(aes), emptyList())
            }
        }
    }
}