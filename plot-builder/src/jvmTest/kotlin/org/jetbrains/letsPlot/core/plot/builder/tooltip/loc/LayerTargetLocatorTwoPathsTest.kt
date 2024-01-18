/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertEncodedObjects
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.horizontalPathTarget
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.pathTarget
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.point
import kotlin.test.Test

class LayerTargetLocatorTwoPathsTest {

    @Test
    fun nearestX() {
        val xList = doubleArrayOf(100.0, 101.0, 103.0, 104.0)

        val locator = createLocator(
            LookupStrategy.NEAREST, LookupSpace.X,
            horizontalPathTarget(FIRST_PATH_KEY, 100.0, xList),
            horizontalPathTarget(SECOND_PATH_KEY, 200.0, xList)
        )

        val pointInXRange = point(102.0, 110.0)
        assertEncodedObjects(
            locator, pointInXRange,
            FIRST_PATH_KEY,
            SECOND_PATH_KEY
        )

        val pointOutOfXRange = point(90.0, 150.0)
        assertEncodedObjects(
            locator, pointOutOfXRange,
            FIRST_PATH_KEY,
            SECOND_PATH_KEY
        )
    }

    @Test
    fun nearestXClosePoints() {
        val xList = doubleArrayOf(100.0, 101.1, 101.2, 101.3, 101.4, 101.5, 103.0, 104.0)

        val locator = createLocator(
            LookupStrategy.NEAREST, LookupSpace.X,
            horizontalPathTarget(FIRST_PATH_KEY, 100.0, xList),
            horizontalPathTarget(SECOND_PATH_KEY, 200.0, xList)
        )

        val pointInXRange = point(101.0, 110.0)
        assertEncodedObjects(
            locator, pointInXRange,
            FIRST_PATH_KEY,
            SECOND_PATH_KEY
        )

        val pointOutOfXRange = point(90.0, 150.0)
        assertEncodedObjects(
            locator, pointOutOfXRange,
            FIRST_PATH_KEY,
            SECOND_PATH_KEY
        )
    }

    @Test
    fun hoverXY() {
        val locator = createLocator(
            LookupStrategy.HOVER, LookupSpace.XY,
            pathTarget(FIRST_PATH_KEY, listOf(DoubleVector(100.0, 100.0), DoubleVector(110.0, 110.0), DoubleVector(120.0, 120.0))),
        )

        // Interpolate the point.
        locator.search(point(112.5, 117.5))!!.targets[0].let {
            assertThat(it.hitIndex).isEqualTo(101)
            assertThat(it.tipLayoutHint.coord).isEqualTo(DoubleVector(115, 115))
        }

        // Snap to the nearest point.
        locator.search(point(90.0, 90.0))!!.targets[0].let {
            assertThat(it.hitIndex).isEqualTo(100)
            assertThat(it.tipLayoutHint.coord).isEqualTo(DoubleVector(100, 100))
        }
    }

    companion object {
        private const val FIRST_PATH_KEY = 1
        private const val SECOND_PATH_KEY = 2
    }
}
