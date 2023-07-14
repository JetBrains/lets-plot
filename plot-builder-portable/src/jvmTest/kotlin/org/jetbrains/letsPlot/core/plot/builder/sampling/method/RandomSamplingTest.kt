/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.builder.data.generateData
import kotlin.test.*

class RandomSamplingTest {

    private val myData = generateData(
        N,
        listOf("x", "y", "c")
    )

    @Test
    fun noop() {
        assertTrue(RandomSampling(N - 1, null).isApplicable(myData))

        val failedSampling = RandomSampling(N, null)
        assertFalse(failedSampling.isApplicable(myData))

        assertFailsWith(RuntimeException::class) {
            failedSampling.apply(myData)
        }
    }

    @Test
    fun smallSample() {
        assertRowCount(N_SMALL_SAMPLE)
    }

    @Test
    fun largeSample() {
        assertRowCount(N_LARGE_SAMPLE)
    }

    private fun assertRowCount(sampleSize: Int) {
        val sampleData = RandomSampling(sampleSize, null).apply(myData)
        assertEquals(sampleSize, sampleData.rowCount(), "Row count")
    }

    companion object {
        private const val N = 10
        private const val N_SMALL_SAMPLE = 1
        private const val N_LARGE_SAMPLE = 9
    }
}