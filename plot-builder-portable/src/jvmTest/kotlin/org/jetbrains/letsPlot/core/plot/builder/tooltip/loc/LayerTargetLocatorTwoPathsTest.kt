/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertEncodedObjects
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.horizontalPathTarget
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

    companion object {
        private const val FIRST_PATH_KEY = 1
        private const val SECOND_PATH_KEY = 2
    }
}
