package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import org.assertj.core.api.Java6Assertions.assertThat
import kotlin.test.Test

class LayerTargetLocatorSinglePolygonTest {
    private lateinit var locator: GeomTargetLocator

    @Test
    fun pointInsidePolygon_ShouldReturnPolygonKey() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.TARGET
        )
        assertObjects(locator, point(50.0, 50.0),
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.POLYGON_KEY
        )
    }


    @Test
    fun pointInside_WithNearestStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.TARGET
        )
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    @Test
    fun pointOutside_WithNearestStrategy_ShouldReturnNoTargets() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.TARGET
        )
        assertEmpty(locator, point(150.0, 0.0))
    }

    @Test
    fun pointInside_WithHoverStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.TARGET
        )
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    private fun distanceFor(coord: DoubleVector): Double {
        return locator.search(coord)!!.distance
    }

    companion object {
        private val POLYGON = polygon(
                point(0.0, 0.0),
                point(100.0, 0.0),
                point(100.0, 100.0),
                point(0.0, 100.0))

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorSinglePolygonTest.Companion.POLYGON
        )
    }
}
