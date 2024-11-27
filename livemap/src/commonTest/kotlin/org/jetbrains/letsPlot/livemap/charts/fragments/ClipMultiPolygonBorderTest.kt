/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.charts.fragments

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.geometry.ClipMultiPolygonBorder
import kotlin.test.Test
import kotlin.test.assertEquals

class ClipMultiPolygonBorderTest {

    private val clipRect: Rect<LonLat> = Rect.LTRB(0.0, 0.0, 10.0, 10.0)

    private fun <ItemT> MicroTask<ItemT>.run(): MicroTask<ItemT> {
        while (alive()) {
            resume()
        }
        return this
    }

    @Test
    fun filterTwoDotsLine() {
        val multiPolygon: MultiPolygon<LonLat> = createMultiPolygon(listOf(
            Vec(2.0, 10.0),
            Vec(7.0, 0.0),
            Vec(10.0, 0.0),
            Vec(10.0, 10.0),
            Vec(2.0, 10.0),
        ))

        val task = ClipMultiPolygonBorder(multiPolygon, clipRect).run()

        assertEquals(createMultiLineString(listOf(
            Vec(2.0, 10.0),
            Vec(7.0, 0.0),
        )), task.getResult())
    }

    @Test
    fun filterThreeDotsLine() {
        val multiPolygon: MultiPolygon<LonLat> = createMultiPolygon(listOf(
            Vec(2.0, 10.0),
            Vec(3.0, 4.0),
            Vec(7.0, 0.0),
            Vec(10.0, 0.0),
            Vec(10.0, 10.0),
            Vec(2.0, 10.0),
        ))

        val task = ClipMultiPolygonBorder(multiPolygon, clipRect).run()

        assertEquals(createMultiLineString(listOf(
            Vec(2.0, 10.0),
            Vec(3.0, 4.0),
            Vec(7.0, 0.0),
        )), task.getResult())
    }
}