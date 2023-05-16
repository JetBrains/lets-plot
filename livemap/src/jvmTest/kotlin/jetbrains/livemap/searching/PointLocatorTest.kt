/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.PointLocator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PointLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15)
    private val manager = EcsComponentManager()
    private val locator = PointLocator
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
        assertThat(locator.search(viewport.getViewCoord(Vec(5, 5)), entity, viewport)).isNotNull
    }

    @Test
    fun coordinateOutOfMarker() {
        assertThat(locator.search(explicitVec(10.0, 10.0), entity, viewport)).isNull()
    }
}