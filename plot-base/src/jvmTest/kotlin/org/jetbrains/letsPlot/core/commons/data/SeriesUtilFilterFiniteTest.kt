/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(Parameterized::class)
class SeriesUtilFilterFiniteTest(
    private val list0: List<Double?>,
    private val list1: List<Double?>,
    private val expected: List<List<Double>>,   // Two output lists
) {

    @Test
    fun filterFinite() {
        val filtered = SeriesUtil.filterFinite(list0, list1)
        assertEquals(expected[0], filtered[0])
        assertEquals(expected[1], filtered[1])

        if (list0.size == filtered[0].size) {
            assertSame(list0, filtered[0])
            assertSame(list1, filtered[1])
        }
    }


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                arrayOf(
                    emptyList<Double?>(),
                    emptyList<Double?>(),
                    listOf(emptyList<Double>(), emptyList<Double>())
                ),
                arrayOf(
                    listOf<Double?>(null, Double.NEGATIVE_INFINITY, Double.NaN),
                    listOf<Double?>(1.0, 1.0, 1.0),
                    listOf(emptyList<Double>(), emptyList<Double>())
                ),
                arrayOf(
                    listOf<Double?>(1.0, 1.0, 1.0),
                    listOf<Double?>(null, Double.NEGATIVE_INFINITY, Double.NaN),
                    listOf(emptyList<Double>(), emptyList<Double>())
                ),
                arrayOf(
                    listOf<Double?>(null, 1.0, Double.NaN),
                    listOf<Double?>(1.0, Double.NEGATIVE_INFINITY, 1.0),
                    listOf(emptyList<Double>(), emptyList<Double>())
                ),
                arrayOf(
                    listOf<Double?>(null, 1.0, Double.NaN),
                    listOf<Double?>(1.0, 2.0, Double.NEGATIVE_INFINITY),
                    listOf(listOf<Double>(1.0), listOf<Double>(2.0))
                ),
                arrayOf(
                    listOf<Double?>(1.0, 2.0),
                    listOf<Double?>(3.0, 4.0),
                    listOf(listOf<Double>(1.0, 2.0), listOf<Double>(3.0, 4.0))
                ),
                arrayOf(
                    listOf<Double?>(1.0, null),
                    listOf<Double?>(2.0, Double.NEGATIVE_INFINITY),
                    listOf(listOf<Double>(1.0), listOf<Double>(2.0))
                ),
                arrayOf(
                    listOf<Double?>(Double.NaN, 1.0),
                    listOf<Double?>(null, 2.0),
                    listOf(listOf<Double>(1.0), listOf<Double>(2.0))
                ),
            )
        }
    }
}
