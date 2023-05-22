/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.LineString
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.PathLocator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PathLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15).apply {
        zoom = 1
        position = Vec(128, 128)
    }
    private val manager = EcsComponentManager()
    private val locator = PathLocator
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
        assertThat(locator.search(Vec(128, 128), entity, viewport)).isNotNull
    }

    @Test
    fun coordinateOutOfPath() {
        assertThat(locator.search(Vec(30, 20), entity, viewport)).isNull()
    }
}