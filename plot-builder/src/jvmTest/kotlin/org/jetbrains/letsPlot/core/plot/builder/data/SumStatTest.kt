/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.junit.Test

class SumStatTest {
    @Test
    fun emptyDataFrame() {
        val dataProcessor = DataProcessor()
        val x = dataProcessor.putVariable(name = "x", values = emptyList(), mappingAes = Aes.X)
        val y = dataProcessor.putVariable(name = "y", values = emptyList(), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.sum())

        assertThat(statDf.variables())
            .containsExactlyInAnyOrder(x, y, Stats.X, Stats.Y, Stats.N, Stats.PROP, Stats.PROPPCT)

        assertThat(statDf.rowCount()).isZero()
    }

    @Test
    fun checkStatVars() {
        val dataProcessor = DataProcessor()
        val x = dataProcessor.putVariable(name = "x", values = listOf("0"), mappingAes = Aes.X)
        val y = dataProcessor.putVariable(name = "y", values = listOf("0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.sum())

        assertThat(statDf.variables())
            .containsExactlyInAnyOrder(x, y, Stats.X, Stats.Y, Stats.N, Stats.PROP, Stats.PROPPCT)

        assertThat(statDf[Stats.X]).containsExactly("0")
        assertThat(statDf[Stats.Y]).containsExactly("0")
        assertThat(statDf[Stats.N]).containsExactly(1.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
        assertThat(statDf[Stats.PROPPCT]).containsExactly(100.0)
    }

    @Test
    fun simple() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "1"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.sum())

        assertThat(statDf[Stats.N]).containsExactly(1.0, 1.0)
        assertThat(statDf[Stats.PROP]).containsExactly(0.5, 0.5)
    }

    @Test
    fun overlapped() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.sum())

        assertThat(statDf[Stats.N]).containsExactly(2.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
    }

    @Test
    fun `nulls completely ignored`() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0", null), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0", "0"), mappingAes = Aes.Y)

        val statDf = dataProcessor.applyStat(Stats.sum())

        assertThat(statDf[Stats.N]).containsExactly(2.0)
        assertThat(statDf[Stats.PROP]).containsExactly(1.0)
    }

    @Test
    fun grouping() {
        val dataProcessor = DataProcessor()
        dataProcessor.putVariable(name = "x", values = listOf("0", "0", "1"), mappingAes = Aes.X)
        dataProcessor.putVariable(name = "y", values = listOf("0", "0", "0"), mappingAes = Aes.Y)

        // No grouping
        dataProcessor.groupingVarName = null
        dataProcessor.applyStat(Stats.sum()).let { statDf ->
            assertThat(statDf[Stats.N]).containsExactly(2.0, 1.0)
            assertThat(statDf[Stats.PROP]).containsExactly(0.6666666666666666, 0.3333333333333333)
        }

        // Group columns
        dataProcessor.groupingVarName = "x"
        dataProcessor.applyStat(Stats.sum()).let { statDf ->
            assertThat(statDf[Stats.N]).containsExactly(2.0, 1.0)
            assertThat(statDf[Stats.PROP]).containsExactly(1.0, 1.0)
        }

        // Group rows
        dataProcessor.groupingVarName = "y"
        dataProcessor.applyStat(Stats.sum()).let { statDf ->
            assertThat(statDf[Stats.N]).containsExactly(2.0, 1.0)
            assertThat(statDf[Stats.PROP]).containsExactly(0.6666666666666666, 0.3333333333333333)
        }
    }
}