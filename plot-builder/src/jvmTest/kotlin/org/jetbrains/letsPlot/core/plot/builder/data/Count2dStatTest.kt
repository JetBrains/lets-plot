/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.junit.Test

class Count2dStatTest {
    @Test
    fun emptyDataFrame() {
        val dataProcessor = DataProcessor()
        val x = dataProcessor.putVariable(name = "x", values = emptyList(), mappingAes = Aes.X)
        val y = dataProcessor.putVariable(name = "y", values = emptyList(), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf.variables())
            .containsExactlyInAnyOrder(x, y, Stats.X, Stats.Y, Stats.SUM, Stats.COUNT, Stats.PROP, Stats.PROPPCT)

        assertThat(statDf.rowCount()).isZero()
    }

    @Test
    fun checkStatVars() {
        val dataProcessor = DataProcessor()
        val x = dataProcessor.putVariable(name = "x", values = listOf("0"), mappingAes = Aes.X)
        val y = dataProcessor.putVariable(name = "y", values = listOf("0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf.variables())
            .containsExactlyInAnyOrder(x, y, Stats.X, Stats.Y, Stats.SUM, Stats.COUNT, Stats.PROP, Stats.PROPPCT)

        assertThat(statDf[Stats.X]).containsExactly("0")
        assertThat(statDf[Stats.Y]).containsExactly("0")
        assertThat(statDf[Stats.SUM]).containsExactly(1.0)
        assertThat(statDf[Stats.COUNT]).containsExactly(1.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
        assertThat(statDf[Stats.PROPPCT]).containsExactly(100.0)
    }

    @Test
    fun simple() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "1"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf[Stats.SUM]).containsExactly(1.0, 1.0)
        assertThat(statDf[Stats.COUNT]).containsExactly(1.0, 1.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0, 1.0)
    }

    @Test
    fun overlapped() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf[Stats.SUM]).containsExactly(2.0)
        assertThat(statDf[Stats.COUNT]).containsExactly(2.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
    }

    @Test
    fun `overlapped weighted`() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0"), mappingAes = Aes.Y)
        dataProcessor.putVariable(name = "w", values = listOf(1.0, 3.0), mappingAes = Aes.WEIGHT)

        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf[Stats.SUM]).containsExactly(4.0)
        assertThat(statDf[Stats.COUNT]).containsExactly(4.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
    }

    @Test
    fun `overlapped weighted grouped`() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0"), mappingAes = Aes.Y)
        dataProcessor.putVariable(name = "w", values = listOf(1.0, 3.0), mappingAes = Aes.WEIGHT)
        dataProcessor.putVariable(name = "g", values = listOf("A", "B"))

        dataProcessor.groupingVarName = "g"
        val statDf = dataProcessor.applyStat(Stats.count2d())

        assertThat(statDf[Stats.SUM]).containsExactly(4.0, 4.0)
        assertThat(statDf[Stats.COUNT]).containsExactly(1.0, 3.0)
        assertThat(statDf[Stats.PROP]).containsExactly(0.25, 0.75)
    }

    @Test
    fun `nulls completely ignored`() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0", null), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0", "0"), mappingAes = Aes.Y)
        dataProcessor.putVariable(name = "w", values = listOf(1.0, 3.0, 5.0), mappingAes = Aes.WEIGHT)
        dataProcessor.putVariable(name = "g", values = listOf("A", "B", "B"))

        dataProcessor.groupingVarName = null
        dataProcessor.applyStat(Stats.count2d()).let { statDf ->
            assertThat(statDf[Stats.SUM]).containsExactly(4.0)
            assertThat(statDf[Stats.COUNT]).containsExactly(4.0)
            assertThat(statDf[Stats.PROP]).containsExactly(1.0)
        }

        dataProcessor.groupingVarName = "g"
        dataProcessor.applyStat(Stats.count2d()).let { statDf ->
            assertThat(statDf[Stats.SUM]).containsExactly(4.0, 4.0)
            assertThat(statDf[Stats.COUNT]).containsExactly(1.0, 3.0)
            assertThat(statDf[Stats.PROP]).containsExactly(0.25, 0.75)
        }
    }
}