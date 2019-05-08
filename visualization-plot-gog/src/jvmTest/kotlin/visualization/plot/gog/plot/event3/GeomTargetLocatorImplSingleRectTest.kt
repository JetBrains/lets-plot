package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.inside
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.outsideX
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.outsideXY
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.outsideY
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.rectTarget
import kotlin.test.Test


class GeomTargetLocatorImplSingleRectTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator, inside(RECT), RECT_KEY)

        // Not match
        assertEmpty(locator, outsideY(RECT))
        assertEmpty(locator, outsideX(RECT))
        assertEmpty(locator, outsideXY(RECT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator, inside(RECT), RECT_KEY)
        assertObjects(locator, outsideY(RECT), RECT_KEY)
        assertObjects(locator, outsideX(RECT), RECT_KEY)
        assertObjects(locator, outsideXY(RECT), RECT_KEY)
    }

    @Test
    fun hoverXAndNearestXHaveSameBehaviour() {
        for (strategy in listOf(LookupStrategy.HOVER, LookupStrategy.NEAREST)) {
            val locator = createLocator(strategy, LookupSpace.X)
            assertObjects(locator, inside(RECT), RECT_KEY)
            assertObjects(locator, outsideY(RECT), RECT_KEY)

            assertEmpty(locator, outsideX(RECT))
            assertEmpty(locator, outsideXY(RECT))
        }
    }

    companion object {

        private val RECT = DoubleRectangle(0.0, 100.0, 20.0, 40.0)
        private const val RECT_KEY = 1
        private val TARGET = rectTarget(RECT_KEY, RECT)

        private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
            return TestUtil.createLocator(strategy, space, TARGET)
        }
    }
}
