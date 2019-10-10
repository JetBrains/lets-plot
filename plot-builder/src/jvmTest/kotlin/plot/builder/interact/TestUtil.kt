package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.function.Functions.identity
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.base.interact.HitShape
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Companion.variable
import jetbrains.datalore.plot.builder.interact.MappedDataAccessMock.Mapping
import jetbrains.datalore.plot.builder.interact.loc.TargetPrototype
import org.assertj.core.api.Condition
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestUtil {
    private const val VARIABLE_NAME = "A"
    private const val VARIABLE_VALUE = "value"

    internal fun <T> continuous(aes: Aes<T>): Mapping<T> {
        return jetbrains.datalore.plot.builder.interact.TestUtil.mappedData(aes, true)
    }

    internal fun <T> discrete(aes: Aes<T>): Mapping<T> {
        return jetbrains.datalore.plot.builder.interact.TestUtil.mappedData(aes, false)
    }

    private fun <T> mappedData(aes: Aes<T>, isContinuous: Boolean): Mapping<T> {
        return variable().name(jetbrains.datalore.plot.builder.interact.TestUtil.VARIABLE_NAME).value(jetbrains.datalore.plot.builder.interact.TestUtil.VARIABLE_VALUE).isContinuous(isContinuous).mapping(aes)
    }

    internal fun assertText(tooltipSpecs: List<jetbrains.datalore.plot.builder.interact.TooltipSpec>, vararg expectedTooltipText: String) {
        jetbrains.datalore.plot.builder.interact.TestUtil.assertText(tooltipSpecs, listOf(*expectedTooltipText))
    }

    @SafeVarargs
    internal fun assertText(tooltipSpecs: List<jetbrains.datalore.plot.builder.interact.TooltipSpec>, vararg expectedTooltips: List<String>) {
        assertEquals(expectedTooltips.size.toLong(), tooltipSpecs.size.toLong())
        var i = 0
        val n = tooltipSpecs.size
        while (i < n) {
            val tooltipText = tooltipSpecs[i].lines
            jetbrains.datalore.plot.builder.interact.TestUtil.assertListsEqual(expectedTooltips[i], tooltipText)
            ++i
        }
    }

    private fun <T> assertListsEqual(expected: List<T>, actual: List<T>) {
        assertEquals(expected.size.toLong(), actual.size.toLong())
        var i = 0
        val n = expected.size
        while (i < n) {
            assertEquals(expected[i], actual[i])
            ++i
        }
    }

    internal fun assertNoTooltips(tooltipSpecs: List<jetbrains.datalore.plot.builder.interact.TooltipSpec>) {
        assertTrue(tooltipSpecs.isEmpty())
    }


    private const val OUTSIDE_DELTA = 10.0
    private const val PATH_POINTS_COUNT_PER_KEY = 100

    internal fun createLocator(lookupStrategy: LookupStrategy,
                               lookupSpace: LookupSpace,
                               vararg list: TargetPrototype): GeomTargetLocator {

        val targetsList = ArrayList<TargetPrototype>()
        targetsList.addAll(list)

        val geomKind = GeomKind.POINT
        val lookupSpec = LookupSpec(lookupSpace, lookupStrategy)
        val contextualMapping = mock(ContextualMapping::class.java)

        return jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator(
            geomKind,
            lookupSpec,
            contextualMapping,
            targetsList
        )
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
        return rect.center.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                rect.width / 2 + jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA,
                0.0
            )
        )
    }

    internal fun outsideX(rect: DoubleRectangle, delta: Double): DoubleVector {
        return rect.origin.add(jetbrains.datalore.plot.builder.interact.TestUtil.point(rect.right + delta, 0.0))
    }

    internal fun outsideY(rect: DoubleRectangle): DoubleVector {
        return rect.center.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                0.0,
                rect.height / 2 + jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA
            )
        )
    }

    internal fun outsideXY(rect: DoubleRectangle): DoubleVector {
        return rect.center.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                rect.width + jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA,
                rect.height / 2 + jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA
            )
        )
    }

    private fun betweenX(rect1: DoubleRectangle, rect2: DoubleRectangle): Double {
        return jetbrains.datalore.plot.builder.interact.TestUtil.between(rect1.left, rect2.left)
    }

    private fun betweenY(rect1: DoubleRectangle, rect2: DoubleRectangle): Double {
        return jetbrains.datalore.plot.builder.interact.TestUtil.between(rect1.top, rect2.top)
    }

    internal fun between(rect1: DoubleRectangle, rect2: DoubleRectangle): DoubleVector {
        return DoubleVector(
            jetbrains.datalore.plot.builder.interact.TestUtil.betweenX(rect1, rect2),
            jetbrains.datalore.plot.builder.interact.TestUtil.betweenY(rect1, rect2)
        )
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
        return p.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA,
                0.0
            )
        )
    }

    internal fun offsetX(p: DoubleVector, delta: Double): DoubleVector {
        return p.add(jetbrains.datalore.plot.builder.interact.TestUtil.point(delta, 0.0))
    }

    internal fun offsetY(p: DoubleVector): DoubleVector {
        return p.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                0.0,
                jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA
            )
        )
    }

    internal fun offsetXY(p: DoubleVector): DoubleVector {
        return p.add(
            jetbrains.datalore.plot.builder.interact.TestUtil.point(
                jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA,
                jetbrains.datalore.plot.builder.interact.TestUtil.OUTSIDE_DELTA
            )
        )
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
        return jetbrains.datalore.plot.builder.interact.TestUtil.getGeomTargets(locator.search(p))
    }

    internal fun assertEncodedObjects(locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects(true, locator, coord, *key)
    }

    internal fun assertObjects(locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects(false, locator, coord, *key)
    }

    private fun assertObjects(encoded: Boolean, locator: GeomTargetLocator, coord: DoubleVector, vararg key: Any) {
        val decode = if (encoded) { it -> jetbrains.datalore.plot.builder.interact.TestUtil.decodeKey(it) } else identity<Int>()

        val located = jetbrains.datalore.plot.builder.interact.TestUtil.getGeomTargets(locator.search(coord))

        assertEquals(key.size, located.size)

        var i = 0
        val n = key.size
        while (i < n) {
            val geomTarget = located[i]
            assertEquals(key[i], decode(geomTarget.hitIndex), "Hit index: $i")
            i++
        }
    }

    private fun getGeomTargets(result: LookupResult?): List<GeomTarget> {
        return result?.targets ?: emptyList()
    }

    internal fun assertEmpty(locator: GeomTargetLocator, coord: DoubleVector) {
        val located = jetbrains.datalore.plot.builder.interact.TestUtil.getGeomTargets(locator.search(coord))
        assertTrue(located.isEmpty())
    }

    internal fun rectTarget(key: Any, rect: DoubleRectangle): TargetPrototype {
        val rectShape = HitShape.rect(rect)
        return TargetPrototype(rectShape, { key as Int }, params())
    }

    internal fun pointTarget(key: Any, p: DoubleVector): TargetPrototype {
        val pointShape = HitShape.point(p, 0.0)
        return TargetPrototype(pointShape, { key as Int }, params())
    }

    internal fun pathTarget(key: Int, points: List<DoubleVector>): TargetPrototype {
        val pathShape = HitShape.path(points, false)
        return TargetPrototype(pathShape, { hitIndex ->
            jetbrains.datalore.plot.builder.interact.TestUtil.encodeIndex(
                key,
                hitIndex
            )
        }, params())
    }

    internal fun pathTarget(points: List<DoubleVector>): TargetPrototype {
        val pathShape = HitShape.path(points, false)
        return TargetPrototype(pathShape, identity(), params())
    }

    private fun encodeIndex(key: Int, integer: Int?): Int {
        return key * jetbrains.datalore.plot.builder.interact.TestUtil.PATH_POINTS_COUNT_PER_KEY + integer!!
    }

    private fun decodeKey(index: Int?): Int {
        return index!! / jetbrains.datalore.plot.builder.interact.TestUtil.PATH_POINTS_COUNT_PER_KEY
    }

    private fun decodeIndex(index: Int?): Int {
        return index!! % jetbrains.datalore.plot.builder.interact.TestUtil.PATH_POINTS_COUNT_PER_KEY
    }

    internal fun pathTarget(points: List<DoubleVector>, indexMapper: (Int) -> Int): TargetPrototype {
        val pathShape = HitShape.path(points, false)
        return TargetPrototype(pathShape, indexMapper, params())
    }

    internal fun polygonTarget(key: Int, points: List<DoubleVector>): TargetPrototype {
        val polygonShape = HitShape.path(points, true)
        return TargetPrototype(polygonShape, { key }, params())
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
            jetbrains.datalore.plot.builder.interact.TestUtil.closePathIfNeeded(points)
            pointsList.addAll(points)
        }
        return pointsList
    }

    internal fun polygon(vararg points: DoubleVector): MutableList<DoubleVector> {
        val pointsList = jetbrains.datalore.plot.builder.interact.TestUtil.path(*points)
        jetbrains.datalore.plot.builder.interact.TestUtil.closePathIfNeeded(pointsList)
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

    internal fun horizontalPathTarget(key: Int, y: Double, xList: DoubleArray): TargetPrototype {
        val pathPoints = jetbrains.datalore.plot.builder.interact.TestUtil.horizontalPath(y, *xList)
        return jetbrains.datalore.plot.builder.interact.TestUtil.pathTarget(key, pathPoints)
    }

    internal fun pathTarget(key: Int, vararg points: jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint): TargetPrototype {
        return jetbrains.datalore.plot.builder.interact.TestUtil.pathTarget(key, points.map { it.coord })
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

        fun defaultX(x: Double): jetbrains.datalore.plot.builder.interact.TestUtil.PathPointsBuilder {
            defaultX = x
            return this
        }

        fun defaultY(y: Double): jetbrains.datalore.plot.builder.interact.TestUtil.PathPointsBuilder {
            defaultY = y
            return this
        }

        fun x(x: Double): jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint {
            return jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint(DoubleVector(x, defaultY!!), counter++)
        }

        fun y(y: Double): jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint {
            return jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint(DoubleVector(defaultX!!, y), counter++)
        }

        fun xy(x: Double, y: Double): jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint {
            return jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint(DoubleVector(x, y), counter++)
        }
    }

    internal class HitIndex(private val myExpected: Int) : Condition<GeomTarget>() {

        override fun matches(geomTarget: GeomTarget): Boolean {
            val hitIndex = geomTarget.hitIndex
            return myExpected == jetbrains.datalore.plot.builder.interact.TestUtil.decodeIndex(hitIndex)
        }

        companion object {
            fun equalTo(expected: Int): jetbrains.datalore.plot.builder.interact.TestUtil.HitIndex {
                return jetbrains.datalore.plot.builder.interact.TestUtil.HitIndex(expected)
            }
        }

    }
}