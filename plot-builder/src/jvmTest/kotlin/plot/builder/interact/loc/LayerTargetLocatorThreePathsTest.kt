package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEncodedObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.horizontalPathTarget
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import kotlin.test.Test

class LayerTargetLocatorThreePathsTest {

    @Test
    fun nearestXy() {

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY,
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.FIRST_PATH_KEY, 100.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SHORT_RANGE
                ),
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SECOND_PATH_KEY, 200.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SHORT_RANGE
                ),
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.THIRD_PATH_KEY, 300.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SHORT_RANGE
                )
        )

        val closerToFirstPath = point(102.0, 120.0)
        assertEncodedObjects(locator, closerToFirstPath,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.FIRST_PATH_KEY
        )

        val closerToThirdPath = point(102.0, 280.0)
        assertEncodedObjects(locator, closerToThirdPath,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.THIRD_PATH_KEY
        )
    }

    @Test
    fun nearestXOneShorter() {

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.FIRST_PATH_KEY, 100.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SHORT_RANGE
                ),
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SECOND_PATH_KEY, 200.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.LONG_RANGE
                ),
                horizontalPathTarget(
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.THIRD_PATH_KEY, 300.0,
                    jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.LONG_RANGE
                )
        )

        val pointOnFirstAndSecondPathByX = point(130.0, 200.0)
        assertEncodedObjects(locator, pointOnFirstAndSecondPathByX,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SECOND_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.THIRD_PATH_KEY
        )

        val outOfAllPathsByX = point(140.0, 200.0)
        assertEncodedObjects(locator, outOfAllPathsByX,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.FIRST_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.SECOND_PATH_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorThreePathsTest.Companion.THIRD_PATH_KEY
        )
    }

    companion object {
        private val FIRST_PATH_KEY = 1
        private val SECOND_PATH_KEY = 2
        private val THIRD_PATH_KEY = 3

        private val SHORT_RANGE = doubleArrayOf(100.0, 101.0, 103.0, 104.0)
        private val LONG_RANGE = doubleArrayOf(100.0, 110.0, 120.0, 130.0)
    }
}
