/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.placement.ScreenDimensionComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.searching.PointLocatorHelper
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PointLocatorHelperTest {
    private val manager = EcsComponentManager()
    private val helper = PointLocatorHelper()
    private val radius = 10.0
    private val entity = manager.createEntity("")
        .addComponents {
            + ScreenDimensionComponent().apply { dimension = explicitVec(radius * 2, radius * 2) }
            + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
        }


    @Test
    fun coordinateInMarker() {
        assertTrue { helper.isCoordinateInTarget(explicitVec(5.0, 5.0), entity) }
    }

    @Test
    fun coordinateOutOfMarker() {
        assertFalse { helper.isCoordinateInTarget(explicitVec(10.0, 10.0), entity) }
    }
}