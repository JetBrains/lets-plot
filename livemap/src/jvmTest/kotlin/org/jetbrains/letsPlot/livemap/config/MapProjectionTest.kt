/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.config

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Utils.square
import org.jetbrains.letsPlot.gis.geoprotocol.Boundary
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.Geographic
import org.jetbrains.letsPlot.livemap.core.Projections.mercator
import org.jetbrains.letsPlot.livemap.geometry.MicroTasks.transform
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.test.assertEquals

class MapProjectionTest {

    @Test
    fun simplePointProjection() {
        val mapProjection = createMapProjection(mercator())
        assertThat(mapProjection.apply(Vec<LonLat>(0, 0)))
            .isEqualTo(Vec<World>(x=127.99999999999999, y=127.99999999999997))
    }

    @Test
    fun simpleGeometryProjection() {
        val mercator = mercator()
        val input = square<LonLat>(1, 2, 30, 40).asMultipolygon()

        val transformTask = transform(input, mercator::apply)
        while(transformTask.alive()) {
            transformTask.resume()
        }

        val expected = Boundary.create<Geographic>(
            MultiPolygon(
                listOf(
                    Polygon(
                        listOf(
                            Ring(
                                listOf(
                                    Vec(x=111319.49079327357, y=222684.20850554455),
                                    Vec(x=3450904.2145914803, y=222684.20850554455),
                                    Vec(x=3450904.2145914803, y=5160979.444049783),
                                    Vec(x=111319.49079327357, y=5160979.444049783),
                                    Vec(x=111319.49079327357, y=222684.20850554455)
                                )
                            )
                        )
                    )
                )
            )
        ).asMultipolygon()

        val actual = transformTask.getResult()
        assertEquals(expected, actual)
    }

}