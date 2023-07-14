/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameAssert
import org.jetbrains.letsPlot.core.plot.base.data.generateData
import org.jetbrains.letsPlot.core.plot.base.data.indices
import org.jetbrains.letsPlot.core.plot.base.data.toSerie
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

class DataFrameTest {

    private lateinit var myData: DataFrame
    private lateinit var mySelectIndices: List<Int>
    private lateinit var myAllIndices: Set<Int>

    @BeforeTest
    fun setUp() {
        myData = generateData(N, listOf("x", "y", "c"))
        mySelectIndices = listOf(2, 5, 8)
        myAllIndices = HashSet(indices(N))
    }


    @Test
    fun noop() {
        assertSame(myData, myData.dropIndices(emptySet()))
    }

    @Test
    fun selectIndices() {
        assertThat(myData.selectIndices(HashSet(mySelectIndices)))
            .hasRowCount(3)
            .hasSerie("x", toSerie("x", mySelectIndices))
            .hasSerie("y", toSerie("y", mySelectIndices))
            .hasSerie("c", toSerie("c", mySelectIndices))
    }

    @Test
    fun dropIndices() {
        val dropIndices = myAllIndices.minus(mySelectIndices)
        assertThat(myData.dropIndices(dropIndices))
            .hasRowCount(3)
            .hasSerie("x", toSerie("x", mySelectIndices))
            .hasSerie("y", toSerie("y", mySelectIndices))
            .hasSerie("c", toSerie("c", mySelectIndices))
    }

    @Test
    fun emptyData() {
        assertThat(DataFrame.Builder.emptyFrame())
            .hasRowCount(0)
    }

    companion object {
        private const val N = 10

        private fun assertThat(data: DataFrame): DataFrameAssert {
            return DataFrameAssert(data)
        }
    }
}