/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import kotlin.test.Test

class PolygonSawTeethUpTest {


    private val polygonLocator: GeomTargetLocator
        get() = createLocator(
            GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TARGET
        )

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.MIDDLE_MIN,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TOP
        ))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.LEFT_MIN,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TOP
        ))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.RIGHT_MIN,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TOP
        ))
    }

    companion object {

        /*
    200    *      *
          / \    / \
         /   \  /   \
        /     \/     \
      0 *-----*------*
        0     100    200
  */

        private const val TOP = 200.0
        private const val BOTTOM = 0.0
        private const val FIRST_TOOTH_PEEK_X = 50.0
        private const val SECOND_TOOTH_PEEK_X = 150.0
        private const val LEFT_MIN = 0.0
        private const val RIGHT_MIN = 200.0
        private const val MIDDLE_MIN = 100.0

        private val POLYGON = polygon(
                point(0.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.BOTTOM),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.FIRST_TOOTH_PEEK_X,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TOP
                ),
                point(100.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.BOTTOM),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.SECOND_TOOTH_PEEK_X,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.TOP
                ),
                point(200.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.BOTTOM)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethUpTest.Companion.POLYGON
        )
    }
}
