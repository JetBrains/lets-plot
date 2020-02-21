/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.createMultiPolygon
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.searching.PolygonLocatorHelper
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PolygonLocatorHelperTest {
    private val manager = EcsComponentManager()
    private val helper = PolygonLocatorHelper()
    private val entity = manager.createEntity("")
        .addComponents {
            + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
            + ScreenGeometryComponent().apply {
                geometry = createMultiPolygon(
                    listOf(
                        explicitVec(0.0, 0.0),
                        explicitVec(4.0, 0.0),
                        explicitVec(4.0, 4.0),
                        explicitVec(0.0, 4.0),
                        explicitVec(0.0, 0.0)
                    )
                )
            }
        }

    @Test
    fun mouseInPolygon() {
        assertTrue { helper.isCoordinateInTarget(explicitVec(2.0, 2.0), entity) }
    }

    @Test
    fun mouseOutOfPolygon() {
        assertFalse { helper.isCoordinateInTarget(explicitVec(-2.0, -2.0), entity) }
    }
}