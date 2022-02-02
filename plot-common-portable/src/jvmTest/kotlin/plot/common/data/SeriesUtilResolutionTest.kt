/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import jetbrains.datalore.plot.common.data.MeshGen.genGrid
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SeriesUtilResolutionTest(
    private val serie: List<Double?>,
    private val expected: Double,
) {

    @Test
    fun test() {
        assertEquals(expected, SeriesUtil.resolution(serie, naValue = 0.0))
    }


    companion object {
        private fun args(
            serie: List<Double?>,
            expected: Double,
        ): Array<Any?> {
            return arrayOf(serie, expected)
        }

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Array<Array<Any?>> {
            val orderedEqualSpace = listOf(1.0, 2.0, 3.0)
            val orderedVarSpace = listOf(1.0, 2.0, 2.5)
            val unOrderedEqualSpace = listOf(1.0, 3.0, 2.0)
            val unOrderedVarSpace = listOf(1.0, 3.0, 2.5)

            return arrayOf(
                args(
                    serie = orderedEqualSpace,
                    expected = 1.0
                ),
                args(
                    serie = orderedVarSpace,
                    expected = 0.5
                ),
                args(
                    serie = unOrderedEqualSpace,
                    expected = 1.0
                ),
                args(
                    serie = unOrderedVarSpace,
                    expected = 0.5
                ),
                args(
                    serie = genGrid(xs = orderedEqualSpace).columns,
                    expected = 1.0
                ),
                args(
                    serie = genGrid(xs = orderedEqualSpace).rows,
                    expected = 10.0
                ),
                args(
                    serie = genGrid(xs = orderedVarSpace).columns,
                    expected = 0.5
                ),
                args(
                    serie = genGrid(xs = orderedVarSpace).rows,
                    expected = 10.0
                ),
                args(
                    serie = genGrid(xs = unOrderedEqualSpace).columns,
                    expected = 1.0
                ),
                args(
                    serie = genGrid(xs = unOrderedEqualSpace).rows,
                    expected = 10.0
                ),
                args(
                    serie = genGrid(xs = unOrderedVarSpace).columns,
                    expected = 0.5
                ),
                args(
                    serie = genGrid(xs = unOrderedVarSpace).rows,
                    expected = 10.0
                ),
            )
        }
    }
}
