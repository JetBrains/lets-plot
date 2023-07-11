/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.point

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.chart.point.PointLocator
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions
import org.junit.Test

class PointLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15)
    private val renderHelper = RenderHelper(viewport)
    private val manager = EcsComponentManager()
    private val radius = 10.0
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +PointComponent().apply { size = radius * 2 }
            +ChartElementComponent().apply { scalingSizeFactor = 1.0 }
            +WorldOriginComponent(Vec(5, 5))
        }


    @Test
    fun coordinateInMarker() {
        Assertions.assertThat(PointLocator.search(viewport.getViewCoord(Vec(5, 5)), entity, renderHelper)).isNotNull
    }

    @Test
    fun coordinateOutOfMarker() {
        Assertions.assertThat(PointLocator.search(explicitVec(10.0, 10.0), entity, renderHelper)).isNull()
    }
}