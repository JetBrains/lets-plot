package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.inside
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideX
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideXY
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideY
import jetbrains.datalore.plot.builder.interact.TestUtil.rectTarget
import kotlin.test.Test


class LayerTargetLocatorSingleRectTest {

    @Test
    fun hoverXy() {
        val locator =
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.createLocator(
                LookupStrategy.HOVER,
                LookupSpace.XY
            )

        assertObjects(locator, inside(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
        )

        // Not match
        assertEmpty(locator, outsideY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT))
        assertEmpty(locator, outsideX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT))
        assertEmpty(locator, outsideXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT))
    }

    @Test
    fun nearestXy() {
        val locator =
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.createLocator(
                LookupStrategy.NEAREST,
                LookupSpace.XY
            )

        assertObjects(locator, inside(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
        )
        assertObjects(locator, outsideY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
        )
        assertObjects(locator, outsideX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
        )
        assertObjects(locator, outsideXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
        )
    }

    @Test
    fun hoverXAndNearestXHaveSameBehaviour() {
        for (strategy in listOf(LookupStrategy.HOVER, LookupStrategy.NEAREST)) {
            val locator =
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.createLocator(
                    strategy,
                    LookupSpace.X
                )
            assertObjects(locator, inside(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
            )
            assertObjects(locator, outsideY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT),
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY
            )

            assertEmpty(locator, outsideX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT))
            assertEmpty(locator, outsideXY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT))
        }
    }

    companion object {

        private val RECT = DoubleRectangle(0.0, 100.0, 20.0, 40.0)
        private const val RECT_KEY = 1
        private val TARGET = rectTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.RECT
        )

        private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
            return jetbrains.datalore.plot.builder.interact.TestUtil.createLocator(strategy, space,
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSingleRectTest.Companion.TARGET
            )
        }
    }
}
