/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.LineString
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.point
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.PathLocator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PathLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15)
    private val manager = EcsComponentManager()
    private val locator = PathLocator
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +ChartElementComponent().apply {
                strokeWidth = 5.0
            }
            +ScreenGeometryComponent().apply {
                geometry = Geometry.of(
                    LineString.of(
                        point(10, 10),
                        point(20, 30),
                        point(50, 20),
                        point(10, 10),
                    )
                )
            }
        }

    @Test
    fun coordinateInPath() {
        assertThat(locator.search(point(40, 20), entity, viewport)).isNotNull
    }

    @Test
    fun coordinateOutOfPath() {
        assertThat(locator.search(point(30, 20), entity, viewport)).isNull()
    }
}