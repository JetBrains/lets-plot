/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.path.PathLocator
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions
import org.junit.Test

class PathLocatorTest {
    private val renderHelper =
        RenderHelper(
            Viewport(
                ViewportHelper(
                    World.DOMAIN,
                    true,
                    myLoopY = false
                ),
                ClientPoint(256, 256),
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