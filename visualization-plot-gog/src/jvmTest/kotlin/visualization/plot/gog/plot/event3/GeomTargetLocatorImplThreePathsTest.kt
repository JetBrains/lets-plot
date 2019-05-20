package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.assertEncodedObjects
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.createLocator
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.horizontalPathTarget
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.point
import kotlin.test.Test

class GeomTargetLocatorImplThreePathsTest {

    @Test
    fun nearestXy() {

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY,
                horizontalPathTarget(FIRST_PATH_KEY, 100.0, SHORT_RANGE),
                horizontalPathTarget(SECOND_PATH_KEY, 200.0, SHORT_RANGE),
                horizontalPathTarget(THIRD_PATH_KEY, 300.0, SHORT_RANGE)
        )

        val closerToFirstPath = point(102.0, 120.0)
        assertEncodedObjects(locator, closerToFirstPath, FIRST_PATH_KEY)

        val closerToThirdPath = point(102.0, 280.0)
        assertEncodedObjects(locator, closerToThirdPath, THIRD_PATH_KEY)
    }

    @Test
    fun nearestXOneShorter() {

        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                horizontalPathTarget(FIRST_PATH_KEY, 100.0, SHORT_RANGE),
                horizontalPathTarget(SECOND_PATH_KEY, 200.0, LONG_RANGE),
                horizontalPathTarget(THIRD_PATH_KEY, 300.0, LONG_RANGE)
        )

        val pointOnFirstAndSecondPathByX = point(130.0, 200.0)
        assertEncodedObjects(locator, pointOnFirstAndSecondPathByX, FIRST_PATH_KEY, SECOND_PATH_KEY, THIRD_PATH_KEY)

        val outOfAllPathsByX = point(140.0, 200.0)
        assertEncodedObjects(locator, outOfAllPathsByX, FIRST_PATH_KEY, SECOND_PATH_KEY, THIRD_PATH_KEY)
    }

    companion object {
        private val FIRST_PATH_KEY = 1
        private val SECOND_PATH_KEY = 2
        private val THIRD_PATH_KEY = 3

        private val SHORT_RANGE = doubleArrayOf(100.0, 101.0, 103.0, 104.0)
        private val LONG_RANGE = doubleArrayOf(100.0, 110.0, 120.0, 130.0)
    }
}
