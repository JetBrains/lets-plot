/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.polygon

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.polygon.PolygonLocator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions
import org.junit.Test

class PolygonLocatorTest {
    private val viewport = Viewport(
        ViewportHelper(
            org.jetbrains.letsPlot.livemap.World.DOMAIN,
            true,
            myLoopY = false
        ),
        org.jetbrains.letsPlot.livemap.ClientPoint(600, 400),
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