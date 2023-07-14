/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.sampling.GroupAwareSampling
import kotlin.test.*

internal class GroupRandomSamplingTest : GroupSamplingTestBase() {

    @BeforeTest
    fun setUp() {
        setUp(
            N,
            N_GROUPS
        )
    }

    private fun assertThat(sampleSize: Int): SamplingAssert {
        return SamplingAssert(sampleSize)
    }

    @Test
    fun noop() {
        assertThat(2).isApplicable
        val napSampling = assertThat(3)
            .isNotApplicable
            .sampling

        assertFailsWith(RuntimeException::class) {
            napSampling.apply(data!!, groupMapper!!)
        }
    }

    @Test
    fun sample() {
        for (i in 1 until N_GROUPS) {
            assertThat(i)
                .isApplicable
                .hasGroupCount(i)
        }
    }


    private inner class SamplingAssert internal constructor(sampleSize: Int) {
        internal val sampling: GroupAwareSampling
        private val mySample: DataFrame?

        internal val isApplicable: SamplingAssert
            get() {
                assertTrue(sampling.isApplicable(data!!, groupMapper!!))
                return this
            }

        internal val isNotApplicable: SamplingAssert
            get() {
                assertFalse(sampling.isApplicable(data!!, groupMapper!!))
                return this
            }

        init {
            sampling = GroupRandomSampling(sampleSize, 1_111L)
            if (sampling.isApplicable(data!!, groupMapper!!)) {
                mySample = sampling.apply(data!!, groupMapper!!)
            } else {
                mySample = null
            }
        }

        internal fun hasGroupCount(expected: Int): SamplingAssert {
            val groupCount = mySample!![GROUP_SERIE_VAR].distinct().count()
            assertEquals(expected, groupCount, "Group count")
            return this
        }
    }

    companion object {
        private const val N = 100
        private const val N_GROUPS = 3
    }

}