/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.ChartSource
import jetbrains.livemap.projections.Client
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun splitMapBarChartTest() {
        val bars = Utils.splitMapBarChart(ChartSource().apply {
            indices = listOf(3, 4, 5)
            lon = 0.0
            lat = 0.0
            radius = 10.0
            values = listOf(-2.0, -0.0, 4.0)
            colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
        }, 4.0)

        val dims = listOf<Vec<Client>>(
            explicitVec(6.0, 5.0),
            explicitVec(6.0, 0.5),
            explicitVec(6.0, 10.0)
        )

        bars.map { it.dimension }.run {
            assertEquals(dims, this)
        }

        val offsets = listOf(
            explicitVec<Client>(-10.0, 0.0),
            explicitVec<Client>(-3.0, -0.5),
            explicitVec<Client>(4.0, -10.0)
        )

        bars.map { it.offset }.run {
            assertEquals(offsets, this)
        }
    }
}