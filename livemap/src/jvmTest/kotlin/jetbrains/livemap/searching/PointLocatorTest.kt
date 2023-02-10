/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.PointLocator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PointLocatorTest {
    private val manager = EcsComponentManager()
    private val locator = PointLocator()
    private val radius = 10.0
    private val entity = manager.createEntity("")
        .addComponents {
            + IndexComponent(0, 0)
            + PointComponent().apply { size = radius * 2 }
            + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
            + ChartElementComponent().apply { scalingSizeFactor = 1.0 }
        }


    @Test
    fun coordinateInMarker() {
        assertThat(locator.search(explicitVec(5.0, 5.0), entity)).isNotNull
    }

    @Test
    fun coordinateOutOfMarker() {
        assertThat(locator.search(explicitVec(10.0, 10.0), entity)).isNull()
    }
}