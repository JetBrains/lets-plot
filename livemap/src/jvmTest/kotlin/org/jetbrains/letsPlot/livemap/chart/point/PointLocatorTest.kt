/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.point

import org.assertj.core.api.Assertions
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.times
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.PointComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.junit.Test

class PointLocatorTest {
    private val viewport = Viewport(ViewportHelper(org.jetbrains.letsPlot.livemap.World.DOMAIN, true, myLoopY = false),
        org.jetbrains.letsPlot.livemap.ClientPoint(256, 256), 1, 15)
    private val renderHelper = RenderHelper(viewport)
    private val manager = EcsComponentManager()
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +PointComponent().apply { size = 10.0.px * 2 }
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