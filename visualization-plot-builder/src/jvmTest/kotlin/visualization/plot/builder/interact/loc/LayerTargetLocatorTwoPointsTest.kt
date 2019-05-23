package jetbrains.datalore.visualization.plot.builder.interact.loc

import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.offsetX
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.offsetXY
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.offsetY
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.point
import jetbrains.datalore.visualization.plot.builder.interact.TestUtil.pointTarget
import kotlin.test.Test

class LayerTargetLocatorTwoPointsTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)

        // Not match
        assertEmpty(locator, offsetX(FIRST_POINT))
        assertEmpty(locator, offsetX(SECOND_POINT))
    }


    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        assertObjects(locator, offsetY(FIRST_POINT), FIRST_POINT_KEY)
        assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        assertObjects(locator, offsetY(SECOND_POINT), SECOND_POINT_KEY)

        // Not match
        assertEmpty(locator, offsetX(FIRST_POINT))
        assertEmpty(locator, offsetX(SECOND_POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        assertObjects(locator, offsetXY(FIRST_POINT), FIRST_POINT_KEY)
        assertObjects(locator, offsetXY(SECOND_POINT), SECOND_POINT_KEY)
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        assertEmpty(locator, offsetXY(FIRST_POINT))
        assertEmpty(locator, offsetXY(SECOND_POINT))
    }

    @Test
    fun nearestXCloseToEachOther() {
        val firstTarget = 1
        val secondTarget = 2

        val locator = TestUtil.createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                pointTarget(firstTarget, point(10.0, 10.0)),
                pointTarget(secondTarget, point(13.0, 10.0)))

        val closerToFirst = point(11.0, 10.0)
        assertObjects(locator, closerToFirst, firstTarget)

        val closerToSecond = point(12.0, 10.0)
        assertObjects(locator, closerToSecond, secondTarget)
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, FIRST_TARGET, SECOND_TARGET)
    }

    companion object {
        private const val FIRST_POINT_KEY = 1
        private val FIRST_POINT = point(10.0, 10.0)
        private val FIRST_TARGET = pointTarget(FIRST_POINT_KEY, FIRST_POINT)
        private const val SECOND_POINT_KEY = 2
        private val SECOND_POINT = point(40.0, 10.0)
        private val SECOND_TARGET = pointTarget(SECOND_POINT_KEY, SECOND_POINT)
    }
}
