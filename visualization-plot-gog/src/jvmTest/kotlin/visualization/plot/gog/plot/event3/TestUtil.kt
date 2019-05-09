package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.base.function.Functions.identity
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.config.event3.GeomTargetInteraction.TooltipAesSpec
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTarget
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.*
import jetbrains.datalore.visualization.plot.gog.core.event3.HitShape
import org.assertj.core.api.Condition
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestUtil {
    private val OUTSIDE_DELTA = 10.0
    private val PATH_POINTS_COUNT_PER_KEY = 100

    internal fun createLocator(lookupStrategy: LookupStrategy, lookupSpace: LookupSpace,
                               vararg list: GeomTargetPrototype): GeomTargetLocator {
        val targetsList = ArrayList<GeomTargetPrototype>()
        targetsList.addAll(list)

        val geomKind = GeomKind.POINT
        val lookupSpec = LookupSpec(lookupSpace, lookupStrategy)
        val tooltipAesSpec = mock(TooltipAesSpec::class.java)

        return GeomTargetLocatorImpl(geomKind, lookupSpec, tooltipAesSpec, targetsList)
    }

    fun coord(x: Double, y: Double): DoubleVector {
        return DoubleVector(x, y)
    }

    fun size(width: Double, height: Double): DoubleVector {
        return DoubleVector(width, height)
    }

    fun rect(pos: DoubleVector, dim: DoubleVector): DoubleRectangle {
        return DoubleRectangle(pos, dim)
    }

    internal fun outsideX(rect: DoubleRectangle): DoubleVector {
        return rect.center.add(point(rect.width / 2 + OUTSIDE_DELTA, 0.0))
    }

    internal fun outsideX(rect: DoubleRectangle, delta: Double): DoubleVector {
        return rect.origin.add(point(rect.right + delta, 0.0))
    }

    internal fun outsideY(rect: DoubleRectangle): DoubleVector {
        return rect.center.add(point(0.0, rect.height / 2 + OUTSIDE_DELTA))
    }

    internal fun outsideXY(rect: DoubleRectangle): DoubleVector {
        return rect.center.add(point(rect.width + OUTSIDE_DELTA, rect.height / 2 + OUTSIDE_DELTA))
    }

    private fun betweenX(rect1: DoubleRectangle, rect2: DoubleRectangle): Double {
        return between(rect1.left, rect2.left)
    }

    private fun betweenY(rect1: DoubleRectangle, rect2: DoubleRectangle): Double {
        return between(rect1.top, rect2.top)
    }

    internal fun between(rect1: DoubleRectangle, rect2: DoubleRectangle): DoubleVector {
        return DoubleVector(betweenX(rect1, rect2), betweenY(rect1, rect2))
    }

    internal fun between(v1: Double, v2: Double): Double {
        if (v1 > v2) {
            throw IllegalArgumentException()
        }

        return v1 + (v2 - v1)
    }

    internal fun middle(v1: Double, v2: Double): Double {
        val halfLength = (v2 - v1) / 2
        return v1 + halfLength
    }

    internal fun inside(rect: DoubleRectangle): DoubleVector {
        return rect.center
    }

    internal fun offsetX(p: DoubleVector): DoubleVector {
        return p.add(point(OUTSIDE_DELTA, 0.0))
    }

    internal fun offsetX(p: DoubleVector, delta: Double): DoubleVector {
        return p.add(point(delta, 0.0))
    }

    internal fun offsetY(p: DoubleVector): DoubleVector {
        return p.add(point(0.0, OUTSIDE_DELTA))
    }

    internal fun offsetXY(p: DoubleVector): DoubleVector {
        return p.add(point(OUTSIDE_DELTA, OUTSIDE_DELTA))
    }

    internal fun <T> first(collection: List<T>): T {
        return collection[0]
    }

    internal fun <T> last(collection: List<T>): T {
        return collection[collection.size - 1]
    }

    internal fun <R, T> map(collection: Collection<T>, mapFunction: (T) -> R): List<R> {
        val result = ArrayList<R>()

        for (item in collection) {
            result.add(mapFunction(item))
        }

        return result
    }

    internal fun findTargets(locator: GeomTargetLocator, p: DoubleVector): List<GeomTarget> {
        return getGeomTargets(locator.findTargets(p))
    }

    internal fun assertEncodedObjects(locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        assertObjects(true, locator, coord, *key)
    }

    internal fun assertObjects(locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        assertObjects(false, locator, coord, *key)
    }

    private fun assertObjects(encoded: Boolean, locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        val decode = if (encoded) { it -> decodeKey(it) } else identity<Int>()

        val located = getGeomTargets(locator.findTargets(coord))

        assertEquals(key.size, located.size)

        var i = 0
        val n = key.size
        while (i < n) {
            val geomTarget = located[i]
            assertEquals(key[i], decode(geomTarget.hitIndex), "Hit index: $i")
            i++
        }
    }

    private fun getGeomTargets(locatedTargets: LocatedTargets?): List<GeomTarget> {
        return locatedTargets?.geomTargets ?: emptyList()
    }

    internal fun assertEmpty(locator: GeomTargetLocator, coord: DoubleVector) {
        val located = getGeomTargets(locator.findTargets(coord))
        assertTrue(located.isEmpty())
    }

    internal fun rectTarget(key: Any, rect: DoubleRectangle): GeomTargetPrototype {
        val rectShape = HitShape.rect(rect)
        return GeomTargetPrototype(rectShape, { key as Int }, params())
    }

    internal fun pointTarget(key: Any, p: DoubleVector): GeomTargetPrototype {
        val pointShape = HitShape.point(p, 0.0)
        return GeomTargetPrototype(pointShape, { index -> key as Int }, params())
    }

    internal fun pathTarget(key: Int, points: List<DoubleVector>): GeomTargetPrototype {
        val pathShape = HitShape.path(points, false)
        return GeomTargetPrototype(pathShape, { hitIndex -> encodeIndex(key, hitIndex) }, params())
    }

    internal fun pathTarget(points: List<DoubleVector>): GeomTargetPrototype {
        val pathShape = HitShape.path(points, false)
        return GeomTargetPrototype(pathShape, identity<Int>(), params())
    }

    private fun encodeIndex(key: Int, integer: Int?): Int {
        return key * PATH_POINTS_COUNT_PER_KEY + integer!!
    }

    private fun decodeKey(index: Int?): Int {
        return index!! / PATH_POINTS_COUNT_PER_KEY
    }

    private fun decodeIndex(index: Int?): Int {
        return index!! % PATH_POINTS_COUNT_PER_KEY
    }

    internal fun pathTarget(points: List<DoubleVector>, indexMapper: (Int) -> Int): GeomTargetPrototype {
        val pathShape = HitShape.path(points, false)
        return GeomTargetPrototype(pathShape, indexMapper, params())
    }

    internal fun polygonTarget(key: Int, points: List<DoubleVector>): GeomTargetPrototype {
        val polygonShape = HitShape.path(points, true)
        return GeomTargetPrototype(polygonShape, { key }, params())
    }

    fun point(x: Double, y: Double): DoubleVector {
        return DoubleVector(x, y)
    }

    private fun path(vararg points: DoubleVector): MutableList<DoubleVector> {
        val pointsList = ArrayList<DoubleVector>()
        pointsList.addAll(points)
        return pointsList
    }

    @SafeVarargs
    internal fun multipolygon(vararg pointsArray: MutableList<DoubleVector>): List<DoubleVector> {
        val pointsList = ArrayList<DoubleVector>()
        for (points in pointsArray) {
            closePathIfNeeded(points)
            pointsList.addAll(points)
        }
        return pointsList
    }

    internal fun polygon(vararg points: DoubleVector): MutableList<DoubleVector> {
        val pointsList = path(*points)
        closePathIfNeeded(pointsList)
        return pointsList
    }

    private fun closePathIfNeeded(points: MutableList<DoubleVector>) {
        if (points[0] != points[points.size - 1]) {
            points.add(points[0])
        }
    }

    internal fun horizontalPath(y: Double, vararg xList: Double): List<DoubleVector> {
        val pathPoints = ArrayList<DoubleVector>()
        for (x in xList) {
            pathPoints.add(DoubleVector(x, y))
        }

        return pathPoints
    }

    internal fun horizontalPathTarget(key: Int, y: Double, xList: DoubleArray): GeomTargetPrototype {
        val pathPoints = horizontalPath(y, *xList)
        return pathTarget(key, pathPoints)
    }

    internal fun pathTarget(key: Int, vararg points: PathPoint): GeomTargetPrototype {
        return pathTarget(key, points.map { it.coord })
    }

    internal class PathPoint(val coord: DoubleVector, val hitIndex: Int) {

        val x: Double
            get() = coord.x

        val y: Double
            get() = coord.y
    }

    internal class PathPointsBuilder {
        private var defaultX: Double? = null
        private var defaultY: Double? = null
        private var counter = 0

        fun defaultX(x: Double): PathPointsBuilder {
            defaultX = x
            return this
        }

        fun defaultY(y: Double): PathPointsBuilder {
            defaultY = y
            return this
        }

        fun x(x: Double): PathPoint {
            return PathPoint(DoubleVector(x, defaultY!!), counter++)
        }

        fun y(y: Double): PathPoint {
            return PathPoint(DoubleVector(defaultX!!, y), counter++)
        }

        fun xy(x: Double, y: Double): PathPoint {
            return PathPoint(DoubleVector(x, y), counter++)
        }
    }

    internal class HitIndex(private val myExpected: Int) : Condition<GeomTarget>() {

        override fun matches(geomTarget: GeomTarget): Boolean {
            val hitIndex = geomTarget.hitIndex
            return myExpected == decodeIndex(hitIndex)
        }

        companion object {
            fun equalTo(expected: Int): HitIndex {
                return HitIndex(expected)
            }
        }

    }
}