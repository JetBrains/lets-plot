package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import kotlin.test.BeforeTest
import kotlin.test.Test

class LayerTargetLocatorTwoDistinctPolygonsTest {
    private lateinit var myLocator: GeomTargetLocator

    @BeforeTest
    fun setUp() {
        myLocator = createLocator(LookupStrategy.HOVER, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.FIRST_TARGET,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.SECOND_TARGET
        )
    }

    @Test
    fun pointInsideFirstPolygon_ShouldReturnFirstPolygonKey() {
        assertObjects(myLocator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.FIRST_POLYGON_POINT_INSIDE,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.FIRST_POLYGON_KEY
        )
    }

    @Test
    fun pointInsideSecondPolygon_ShouldReturnSecondPolygonKey() {
        assertObjects(myLocator,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.SECOND_POLYGON_POINT_INSIDE,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.SECOND_POLYGON_KEY
        )
    }

    companion object {
        private val FIRST_POLYGON = polygon(
                point(0.0, 0.0),
                point(100.0, 0.0),
                point(100.0, 100.0),
                point(0.0, 100.0))

        private const val FIRST_POLYGON_KEY = 1
        private val FIRST_TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.FIRST_POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.FIRST_POLYGON
        )
        private val FIRST_POLYGON_POINT_INSIDE = point(50.0, 50.0)

        private val SECOND_POLYGON = polygon(
                point(200.0, 200.0),
                point(300.0, 300.0),
                point(400.0, 200.0))

        private const val SECOND_POLYGON_KEY = 2
        private val SECOND_TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.SECOND_POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocatorTwoDistinctPolygonsTest.Companion.SECOND_POLYGON
        )
        private val SECOND_POLYGON_POINT_INSIDE = point(300.0, 250.0)
    }
}
