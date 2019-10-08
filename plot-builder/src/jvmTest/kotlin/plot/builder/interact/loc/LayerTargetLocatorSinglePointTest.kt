package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.offsetX
import jetbrains.datalore.plot.builder.interact.TestUtil.offsetY
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.pointTarget
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupStrategy
import kotlin.test.Test

class LayerTargetLocatorSinglePointTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )

        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )
        assertObjects(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )
        assertObjects(locator, offsetY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )

        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT))
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertObjects(locator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )
        assertObjects(locator, offsetY(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY
        )

        assertEmpty(locator, offsetX(jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return jetbrains.datalore.plot.builder.interact.TestUtil.createLocator(strategy, space,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.TARGET
        )
    }

    companion object {
        private val POINT = point(100.0, 100.0)
        private const val POINT_KEY = 1
        private val TARGET = pointTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePointTest.Companion.POINT
        )
    }
}
