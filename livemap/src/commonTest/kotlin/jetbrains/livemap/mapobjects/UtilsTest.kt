/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.ChartSource
import jetbrains.livemap.api.splitMapBarChart
import jetbrains.livemap.projection.Client
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun splitMapBarChartTest() {
        val actualDims = ArrayList<Vec<Client>>()
        val actualOffsets = ArrayList<Vec<Client>>()

        splitMapBarChart(ChartSource().apply {
            indices = listOf(3, 4, 5)
            point = explicitVec(0.0, 0.0)
            radius = 10.0
            values = listOf(-2.0, -0.0, 4.0)
            colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
        }, 4.0) { offset, dimension, _ ->
            actualOffsets.add(offset)
            actualDims.add(dimension)
        }

        val dims = listOf<Vec<Client>>(
            explicitVec(6.0, 5.0),
            explicitVec(6.0, 0.5),
            explicitVec(6.0, 10.0)
        )

        assertEquals(dims, actualDims)

        val offsets = listOf(
            explicitVec<Client>(-10.0, 0.0),
            explicitVec<Client>(-3.0, -0.5),
            explicitVec<Client>(4.0, -10.0)
        )

        assertEquals(offsets, actualOffsets)
    }
}