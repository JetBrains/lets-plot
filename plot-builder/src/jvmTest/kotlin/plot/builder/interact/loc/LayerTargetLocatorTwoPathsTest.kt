/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEncodedObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.horizontalPathTarget
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import kotlin.test.Test

class LayerTargetLocatorTwoPathsTest {

    @Test
    fun nearestX() {
        val xList = doubleArrayOf(100.0, 101.0, 103.0, 104.0)

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                horizontalPathTarget(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY, 100.0, xList),
                horizontalPathTarget(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY, 200.0, xList)
        )

        val pointInXRange = point(102.0, 110.0)
        assertEncodedObjects(locator, pointInXRange,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY
        )

        val pointOutOfXRange = point(90.0, 150.0)
        assertEncodedObjects(locator, pointOutOfXRange,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY
        )
    }

    @Test
    fun nearestXClosePoints() {
        val xList = doubleArrayOf(100.0, 101.1, 101.2, 101.3, 101.4, 101.5, 103.0, 104.0)

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                horizontalPathTarget(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY, 100.0, xList),
                horizontalPathTarget(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY, 200.0, xList)
        )

        val pointInXRange = point(101.0, 110.0)
        assertEncodedObjects(locator, pointInXRange,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY
        )

        val pointOutOfXRange = point(90.0, 150.0)
        assertEncodedObjects(locator, pointOutOfXRange,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPathsTest.Companion.SECOND_PATH_KEY
        )
    }

    companion object {
        private val FIRST_PATH_KEY = 1
        private val SECOND_PATH_KEY = 2
    }
}
