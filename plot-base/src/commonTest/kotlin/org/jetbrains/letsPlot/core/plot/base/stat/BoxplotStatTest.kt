/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoxplotStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d, emptyList())
    }

    private fun df(m: Map<DataFrame.Variable, List<Double>>): DataFrame {
        val b = DataFrame.Builder()
        for (key in m.keys) {
            b.putNumeric(key, m.getValue(key))
        }
        return b.build()
    }

    @Test
    fun emptyDataFrame() {
        val df = df(emptyMap())
        val stat = Stats.boxplot()
        val statDf = stat.apply(df, statContext(df))

        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                val variable = stat.getDefaultMapping(aes)
                assertTrue(statDf.has(variable), "Has var " + variable.name)
                assertEquals(0, statDf[variable].size, "Get var " + variable.name)
            }
        }
    }

    @Test
    fun oneElementDataFrame() {
        val df = df(
            mapOf(
                TransformVar.X to listOf(3.3),
                TransformVar.Y to listOf(4.4)
            )
        )
        val stat = Stats.boxplot()
        val statDf = stat.apply(df, statContext(df))

        // no 'width'
        assertTrue(!statDf.has(Stats.WIDTH))

        val checkVar: (DataFrame.Variable, Double) -> Unit = { variable: DataFrame.Variable, expected: Double ->
            assertTrue(statDf.has(variable), "Has var " + variable.name)
            assertEquals(1, statDf[variable].size, "Size var " + variable.name)
            assertEquals(expected, statDf[variable][0], "Get var " + variable.name)
        }

        checkVar(Stats.X, 3.3)

        val varsY = arrayOf(Stats.MIDDLE, Stats.LOWER, Stats.UPPER, Stats.Y_MIN, Stats.Y_MAX)
        for (`var` in varsY) {
            checkVar(`var`, 4.4)
        }
    }

    @Test
    fun varWidth() {
        val df = df(
            mapOf(
                TransformVar.X to listOf(3.3),
                TransformVar.Y to listOf(4.4)
            )
        )

        val stat = Stats.boxplot(
            computeWidth = true
        )
        val statDf = stat.apply(df, statContext(df))

        // has 'width'
        assertTrue(statDf.has(Stats.WIDTH))
        assertEquals(1, statDf[Stats.WIDTH].size)
        assertEquals(1.0, statDf[Stats.WIDTH][0])
    }

    @Test
    fun noXSeries() {
        // see this issue: https://github.com/JetBrains/lets-plot/issues/325
        val df = df(
            mapOf(
                TransformVar.Y to List<Double>(10) { 2.0 }
            )
        )

        val stat = Stats.boxplot()
        val statDf = stat.apply(df, statContext(df))

        assertEquals(1, statDf[Stats.X].size)
        assertEquals(0.0, statDf[Stats.X][0])   // X defaults to 0.0
        assertEquals(2.0, statDf[Stats.MIDDLE][0])
    }

}