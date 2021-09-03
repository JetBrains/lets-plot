/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.api.Symbol
import jetbrains.livemap.api.splitMapBarChart
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun splitMapBarChartTest() {
        val actualDims = ArrayList<Vec<Client>>()
        val actualOffsets = ArrayList<Vec<Client>>()

        splitMapBarChart(
            Symbol().apply {
                indices = listOf(3, 4, 5)
                point = explicitVec(0.0, 0.0)
                radius = 10.0
                values = listOf(-2.0, -0.0, 4.0)
                colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
            }, 4.0
        ) { _, offset, dimension, _ ->
            actualOffsets.add(offset)
            actualDims.add(dimension)
        }

        val dims = listOf<Vec<Client>>(
            explicitVec(6.666666666666667, 5.0),
            explicitVec(6.666666666666667, 0.0),
            explicitVec(6.666666666666667, 10.0)
        )

        assertEquals(dims, actualDims)

        val offsets = listOf(
            explicitVec<Client>(-10.0, 0.0),
            explicitVec<Client>(-3.333333333333333, 0.0),
            explicitVec<Client>(3.333333333333334, -10.0)
        )

        assertEquals(offsets, actualOffsets)
    }
}