package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.assertEmpty
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.assertObjects
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.createLocator
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.point
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.polygon
import jetbrains.datalore.visualization.plot.gog.plot.event3.TestUtil.polygonTarget
import kotlin.test.Test

class PolygonSawTeethDownTest {

    private val polygonLocator: GeomTargetLocator
        get() = createLocator(GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY, TARGET)

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(MIDDLE_MAX, BOTTOM))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(LEFT_MAX, BOTTOM))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(RIGHT_MAX, BOTTOM))
    }

    @Test
    fun whenOnSecondToothPeekPoint_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(SECOND_TOOTH_PEEK_X, BOTTOM))
    }

    @Test
    fun whenInsideSecondToothPeek_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, point(SECOND_TOOTH_PEEK_X, BOTTOM + 1.0), POLYGON_KEY)
    }

    companion object {

        /*

    200 *-----*------*
         \    /\    /
          \  /  \  /
           \/    \/
      0     *     *
        0     100    200
  */

        private const val TOP = 200.0
        private const val BOTTOM = 0.0
        private const val FIRST_TOOTH_PEEK_X = 50.0
        private const val SECOND_TOOTH_PEEK_X = 150.0
        private const val LEFT_MAX = 0.0
        private const val RIGHT_MAX = 200.0
        private const val MIDDLE_MAX = 100.0

        private val POLYGON = polygon(
                point(0.0, TOP),
                point(FIRST_TOOTH_PEEK_X, BOTTOM),
                point(100.0, TOP),
                point(SECOND_TOOTH_PEEK_X, BOTTOM),
                point(200.0, TOP)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(POLYGON_KEY, POLYGON)
    }
}
