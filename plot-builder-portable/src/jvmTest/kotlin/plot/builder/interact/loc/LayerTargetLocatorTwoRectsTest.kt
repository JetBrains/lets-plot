/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.inside
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideX
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideXY
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideY
import jetbrains.datalore.plot.builder.interact.TestUtil.rectTarget
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class LayerTargetLocatorTwoRectsTest {

    @BeforeTest
    fun setUp() {
        // Preconditions
        assertFalse(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT.contains(outsideY(
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT
            )))
        assertFalse(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT.contains(inside(
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT
            )))
        assertFalse(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT.contains(outsideY(
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT
            )))
        assertFalse(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT.contains(inside(
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT
            )))
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertCoordInsideXRangeIgnoresY(locator)
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertCoordInsideXRangeIgnoresY(locator)
    }

    private fun assertCoordInsideXRangeIgnoresY(locator: GeomTargetLocator) {
        assertObjects(locator, inside(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT_KEY
        )
        assertObjects(locator, outsideY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT_KEY
        )
        assertObjects(locator, outsideY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT_KEY
        )
        assertObjects(locator, inside(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT_KEY
        )


        assertEmpty(locator, outsideX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT))
        assertEmpty(locator, outsideXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT))
        assertEmpty(locator, outsideX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT))
        assertEmpty(locator, outsideXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return jetbrains.datalore.plot.builder.interact.TestUtil.createLocator(strategy, space,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_TARGET,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_TARGET
        )
    }

    companion object {
        private val FIRST_RECT_KEY = 1
        private val FIRST_RECT = DoubleRectangle(0.0, 0.0, 20.0, 40.0)
        private val FIRST_TARGET = rectTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.FIRST_RECT
        )
        private val SECOND_RECT_KEY = 2
        private val SECOND_RECT = DoubleRectangle(80.0, 0.0, 20.0, 300.0)
        private val SECOND_TARGET = rectTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoRectsTest.Companion.SECOND_RECT
        )
    }

}
