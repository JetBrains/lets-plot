package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.inside
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.outsideX
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.outsideXY
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.outsideY
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.rectTarget
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class GeomTargetLocatorImplTwoRectsTest {

    @BeforeTest
    fun setUp() {
        // Preconditions
        assertFalse(FIRST_RECT.contains(outsideY(SECOND_RECT)))
        assertFalse(FIRST_RECT.contains(inside(SECOND_RECT)))
        assertFalse(SECOND_RECT.contains(outsideY(FIRST_RECT)))
        assertFalse(SECOND_RECT.contains(inside(FIRST_RECT)))
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
        assertObjects(locator, inside(FIRST_RECT), FIRST_RECT_KEY)
        assertObjects(locator, outsideY(FIRST_RECT), FIRST_RECT_KEY)
        assertObjects(locator, outsideY(SECOND_RECT), SECOND_RECT_KEY)
        assertObjects(locator, inside(SECOND_RECT), SECOND_RECT_KEY)


        assertEmpty(locator, outsideX(FIRST_RECT))
        assertEmpty(locator, outsideXY(FIRST_RECT))
        assertEmpty(locator, outsideX(SECOND_RECT))
        assertEmpty(locator, outsideXY(SECOND_RECT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, FIRST_TARGET, SECOND_TARGET)
    }

    companion object {
        private val FIRST_RECT_KEY = 1
        private val FIRST_RECT = DoubleRectangle(0.0, 0.0, 20.0, 40.0)
        private val FIRST_TARGET = rectTarget(FIRST_RECT_KEY, FIRST_RECT)
        private val SECOND_RECT_KEY = 2
        private val SECOND_RECT = DoubleRectangle(80.0, 0.0, 20.0, 300.0)
        private val SECOND_TARGET = rectTarget(SECOND_RECT_KEY, SECOND_RECT)
    }

}
