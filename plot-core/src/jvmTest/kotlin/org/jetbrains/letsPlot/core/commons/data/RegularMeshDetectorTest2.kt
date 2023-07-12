/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import jetbrains.datalore.plot.common.data.MeshGen.genGrid
import org.jetbrains.letsPlot.core.commons.data.RegularMeshDetector
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class RegularMeshDetectorTest2(
    private val serie: List<Double?>,
    private val expected: Double,
    private val colomns: Boolean,
) {

    @Test
    fun test() {
        val detector = if (colomns) {
            RegularMeshDetector.tryColumn(serie)
        } else {
            RegularMeshDetector.tryRow(serie)
        }

        assertTrue(detector.isMesh)
        assertEquals(expected, detector.resolution)
    }


    companion object {
        private fun args(
            serie: List<Double?>,
            expected: Double,
            coloumns: Boolean
        ): Array<Any?> {
            return arrayOf(serie, expected, coloumns)
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
                    serie = genGrid(xs = orderedEqualSpace).columns,
                    expected = 1.0,
                    coloumns = true
                ),
                args(
                    serie = genGrid(xs = orderedEqualSpace).rows,
                    expected = 10.0,
                    coloumns = false
                ),
                args(
                    serie = genGrid(xs = orderedVarSpace).columns,
                    expected = 0.5,
                    coloumns = true
                ),
                args(
                    serie = genGrid(xs = orderedVarSpace).rows,
                    expected = 10.0,
                    coloumns = false
                ),
                args(
                    serie = genGrid(xs = unOrderedEqualSpace).columns,
                    expected = 1.0,
                    coloumns = true
                ),
                args(
                    serie = genGrid(xs = unOrderedEqualSpace).rows,
                    expected = 10.0,
                    coloumns = false
                ),
                args(
                    serie = genGrid(xs = unOrderedVarSpace).columns,
                    expected = 0.5,
                    coloumns = true
                ),
                args(
                    serie = genGrid(xs = unOrderedVarSpace).rows,
                    expected = 10.0,
                    coloumns = false
                ),
            )
        }
    }
}