package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.offsetX
import jetbrains.datalore.plot.builder.interact.TestUtil.offsetXY
import jetbrains.datalore.plot.builder.interact.TestUtil.offsetY
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.pointTarget
import kotlin.test.Test

class LayerTargetLocatorTwoPointsTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )

        // Not match
        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT))
        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT))
    }


    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator, offsetY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )
        assertObjects(locator, offsetY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )

        // Not match
        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT))
        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )
        assertObjects(locator, offsetXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator, offsetXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY
        )
        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY
        )
        assertEmpty(locator, offsetXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT))
        assertEmpty(locator, offsetXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT))
    }

    @Test
    fun nearestXCloseToEachOther() {
        val firstTarget = 1
        val secondTarget = 2

        val locator = jetbrains.datalore.plot.builder.interact.TestUtil.createLocator(LookupStrategy.NEAREST, LookupSpace.X,
                pointTarget(firstTarget, point(10.0, 10.0)),
                pointTarget(secondTarget, point(13.0, 10.0)))

        val closerToFirst = point(11.0, 10.0)
        assertObjects(locator, closerToFirst, firstTarget)

        val closerToSecond = point(12.0, 10.0)
        assertObjects(locator, closerToSecond, secondTarget)
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return jetbrains.datalore.plot.builder.interact.TestUtil.createLocator(strategy, space,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_TARGET,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_TARGET
        )
    }

    companion object {
        private const val FIRST_POINT_KEY = 1
        private val FIRST_POINT = point(10.0, 10.0)
        private val FIRST_TARGET = pointTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.FIRST_POINT
        )
        private const val SECOND_POINT_KEY = 2
        private val SECOND_POINT = point(40.0, 10.0)
        private val SECOND_TARGET = pointTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoPointsTest.Companion.SECOND_POINT
        )
    }
}
