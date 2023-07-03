/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.polygon

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.Polygon
import jetbrains.datalore.base.typedGeometry.Ring
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.polygon.PolygonLocator
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions
import org.junit.Test

class PolygonLocatorTest {
    private val viewport = Viewport(
        ViewportHelper(
            World.DOMAIN,
            true,
            myLoopY = false
        ),
        ClientPoint(600, 400),
        1,
        15
    ).apply {
        zoom = 1
        position = Vec(128, 128)
    }

    private val renderHelper = RenderHelper(viewport)
    private val manager = EcsComponentManager()
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +WorldGeometryComponent().apply {
                geometry = Geometry.of(
                    Polygon(
                        Ring.of(
                            Vec(120, 120),
                            Vec(130, 120),
                            Vec(130, 130),
                            Vec(120, 130),
                            Vec(120, 120)
                        )
                    )
                )
            }
        }

    @Test
    fun mouseInPolygon() {
        Assertions.assertThat(PolygonLocator.search(Vec(300.0, 200.0), entity, renderHelper)).isNotNull
    }

    @Test
    fun mouseOutOfPolygon() {
        Assertions.assertThat(PolygonLocator.search(Vec(0.0, 0.0), entity, renderHelper)).isNull()
    }
}