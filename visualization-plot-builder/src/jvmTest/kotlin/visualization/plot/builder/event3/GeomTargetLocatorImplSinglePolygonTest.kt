package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.createLocator
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.point
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.polygon
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.polygonTarget
import org.assertj.core.api.Java6Assertions.assertThat
import kotlin.test.Test

class GeomTargetLocatorImplSinglePolygonTest {
    private lateinit var locator: GeomTargetLocator

    @Test
    fun pointInsidePolygon_ShouldReturnPolygonKey() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        assertObjects(locator, point(50.0, 50.0), POLYGON_KEY)
    }


    @Test
    fun pointInside_WithNearestStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    @Test
    fun pointOutside_WithNearestStrategy_ShouldReturnNoTargets() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        assertEmpty(locator, point(150.0, 0.0))
    }

    @Test
    fun pointInside_WithHoverStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    private fun distanceFor(coord: DoubleVector): Double {
        return locator.findTargets(coord)!!.distance
    }

    companion object {
        private val POLYGON = polygon(
                point(0.0, 0.0),
                point(100.0, 0.0),
                point(100.0, 100.0),
                point(0.0, 100.0))

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(POLYGON_KEY, POLYGON)
    }
}
