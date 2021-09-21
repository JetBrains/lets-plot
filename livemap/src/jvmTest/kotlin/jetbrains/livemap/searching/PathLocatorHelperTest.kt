/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.createMultiPolygon
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.jetbrains.livemap.searching.SearchTestHelper.point
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.PathLocatorHelper
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PathLocatorHelperTest {
    private val manager = EcsComponentManager()
    private val helper = PathLocatorHelper()
    private val entity = manager.createEntity("")
        .addComponents {
            + ChartElementComponent().apply {
                strokeWidth = 5.0
            }
            + ScreenLoopComponent().apply { origins = listOf(explicitVec(0.0, 0.0)) }
            + ScreenGeometryComponent().apply {
                geometry = createMultiPolygon(
                    listOf(
                        point(10, 10),
                        point(20, 30),
                        point(50, 20)
                    )
                )
            }
        }

    @Test
    fun coordinateInPath() {
        assertTrue { helper.isCoordinateInTarget(point(40, 20), entity) }
    }

    @Test
    fun coordinateOutOfPath() {
        assertFalse { helper.isCoordinateInTarget(point(30, 20), entity) }
    }
}