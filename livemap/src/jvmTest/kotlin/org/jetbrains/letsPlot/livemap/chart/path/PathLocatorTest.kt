/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.path.PathLocator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions
import org.junit.Test

class PathLocatorTest {
    private val renderHelper =
        RenderHelper(
            Viewport(
                ViewportHelper(
                    org.jetbrains.letsPlot.livemap.World.DOMAIN,
                    true,
                    myLoopY = false
                ),
                org.jetbrains.letsPlot.livemap.ClientPoint(256, 256),
                1,
                15
            ).apply {
                zoom = 1
                position = Vec(128, 128)
            }
        )
    private val manager = EcsComponentManager()
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +ChartElementComponent().apply {
                strokeWidth = 5.0
            }
            +WorldGeometryComponent().apply {
                geometry = Geometry.of(
                    LineString.of(
                        Vec(110, 110),
                        Vec(120, 120),
                        Vec(130, 130),
                    )
                )
            }
        }

    @Test
    fun coordinateInPath() {
        Assertions.assertThat(PathLocator.search(Vec(128, 128), entity, renderHelper))
            .isNotNull
    }

    @Test
    fun coordinateOutOfPath() {
        Assertions.assertThat(PathLocator.search(Vec(30, 20), entity, renderHelper))
            .isNull()
    }
}