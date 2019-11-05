/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import kotlin.test.Test

class PolygonSawTeethDownTest {

    private val polygonLocator: GeomTargetLocator
        get() = createLocator(
            GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.TARGET
        )

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.MIDDLE_MAX,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
        ))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.LEFT_MAX,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
        ))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.RIGHT_MAX,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
        ))
    }

    @Test
    fun whenOnSecondToothPeekPoint_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.SECOND_TOOTH_PEEK_X,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
        ))
    }

    @Test
    fun whenInsideSecondToothPeek_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, point(jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.SECOND_TOOTH_PEEK_X, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM + 1.0),
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.POLYGON_KEY
        )
    }

    companion object {

        /*

    200 *-----*------*
         \    /\    /
          \  /  \  /
           \/    \/
      0     *     *
        0     100    200
  */

        private const val TOP = 200.0
        private const val BOTTOM = 0.0
        private const val FIRST_TOOTH_PEEK_X = 50.0
        private const val SECOND_TOOTH_PEEK_X = 150.0
        private const val LEFT_MAX = 0.0
        private const val RIGHT_MAX = 200.0
        private const val MIDDLE_MAX = 100.0

        private val POLYGON = polygon(
                point(0.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.TOP),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.FIRST_TOOTH_PEEK_X,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
                ),
                point(100.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.TOP),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.SECOND_TOOTH_PEEK_X,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.BOTTOM
                ),
                point(200.0, jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.TOP)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonSawTeethDownTest.Companion.POLYGON
        )
    }
}
