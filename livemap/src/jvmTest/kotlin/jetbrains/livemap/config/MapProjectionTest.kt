/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.config

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Polygon
import jetbrains.datalore.base.typedGeometry.Ring
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.maps.Utils.square
import jetbrains.gis.geoprotocol.Boundary
import jetbrains.livemap.World
import jetbrains.livemap.config.createMapProjection
import jetbrains.livemap.core.Geographic
import jetbrains.livemap.core.Projections.mercator
import jetbrains.livemap.geometry.MicroTasks.transform
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