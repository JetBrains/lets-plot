/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.Polygon
import jetbrains.datalore.base.typedGeometry.Ring
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.PolygonLocator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PolygonLocatorTest {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15)
    private val manager = EcsComponentManager()
    private val locator = PolygonLocator
    private val entity = manager.createEntity("")
        .addComponents {
            +IndexComponent(0, 0)
            +ScreenGeometryComponent().apply {
                geometry = Geometry.of(
                    Polygon(
                        Ring.of(
                            explicitVec(0.0, 0.0),
                            explicitVec(4.0, 0.0),
                            explicitVec(4.0, 4.0),
                            explicitVec(0.0, 4.0),
                            explicitVec(0.0, 0.0)
                        )
                    )
                )
            }
        }

    @Test
    fun mouseInPolygon() {
        assertThat(locator.search(explicitVec(2.0, 2.0), entity, viewport)).isNotNull
    }

    @Test
    fun mouseOutOfPolygon() {
        assertThat(locator.search(explicitVec(-2.0, -2.0), entity, viewport)).isNull()
    }
}