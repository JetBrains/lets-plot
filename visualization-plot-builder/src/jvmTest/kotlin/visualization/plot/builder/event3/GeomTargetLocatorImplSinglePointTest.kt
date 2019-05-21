package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.offsetX
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.offsetY
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.point
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.pointTarget
import kotlin.test.Test

class GeomTargetLocatorImplSinglePointTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator, POINT, POINT_KEY)

        assertEmpty(locator, offsetX(POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetX(POINT), POINT_KEY)
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetY(POINT), POINT_KEY)

        assertEmpty(locator, offsetX(POINT))
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetY(POINT), POINT_KEY)

        assertEmpty(locator, offsetX(POINT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, TARGET)
    }

    companion object {
        private val POINT = point(100.0, 100.0)
        private const val POINT_KEY = 1
        private val TARGET = pointTarget(POINT_KEY, POINT)
    }
}
