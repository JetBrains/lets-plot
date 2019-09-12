package jetbrains.datalore.visualization.plot.base.geom.util

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Point
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.GeomContext
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.common.geometry.Utils.isClosed
import kotlin.math.max


object GeomUtil {
    val TO_LOCATION_X_Y = { p: DataPointAesthetics -> toLocationOrNull(p.x(), p.y()) }
    val TO_LOCATION_X_YMIN = { p: DataPointAesthetics -> toLocationOrNull(p.x(), p.ymin()) }
    val TO_LOCATION_X_YMAX = { p: DataPointAesthetics -> toLocationOrNull(p.x(), p.ymax()) }
    val TO_LOCATION_X_ZERO = { p: DataPointAesthetics -> toLocationOrNull(p.x(), 0.0) }
    val TO_RECTANGLE = { p: DataPointAesthetics ->
        if (SeriesUtil.allFinite(p.xmin(), p.ymin(), p.xmax(), p.ymax())) {
            rectToGeometry(p.xmin()!!, p.ymin()!!, p.xmax()!!, p.ymax()!!)
        } else {
            emptyList()
        }
    }
    private val ORDERING_X = Ordering.from(object : Comparator<DataPointAesthetics?> {
        override fun compare(a: DataPointAesthetics?, b: DataPointAesthetics?): Int {
            val x1 = a?.x()
            val x2 = b?.x()
            if (x1 == null || x2 == null)
                return 0
            else
                return x1.compareTo(x2)
        }
    })
    private val ORDERING_Y = Ordering.from(object : Comparator<DataPointAesthetics?> {
        override fun compare(a: DataPointAesthetics?, b: DataPointAesthetics?): Int {
            val y1 = a?.y()
            val y2 = b?.y()
            if (y1 == null || y2 == null)
                return 0
            else
                return y1.compareTo(y2)
        }
    })
    private val WITH_X_Y = { pointAes: DataPointAesthetics -> SeriesUtil.allFinite(pointAes.x(), pointAes.y()) }
    private val WITH_Y = { pointAes: DataPointAesthetics -> SeriesUtil.isFinite(pointAes.y()) }

    private fun toLocationOrNull(x: Double?, y: Double?): DoubleVector? {
        return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
            DoubleVector(x!!, y!!)
        } else null
    }

    fun with_X_Y(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> WITH_X_Y.invoke(p) }
    }

    fun with_Y(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> WITH_Y.invoke(p) }
    }

    fun ordered_X(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        if (ORDERING_X.isOrdered(dataPoints)) {
            return dataPoints
        }
        return ORDERING_X.sortedCopy(dataPoints)
    }

    fun ordered_Y(dataPoints: Iterable<DataPointAesthetics>, reversed: Boolean): Iterable<DataPointAesthetics> {
        val ordering = if (reversed) ORDERING_Y.reverse() else ORDERING_Y
        if (ordering.isOrdered(dataPoints)) {
            return dataPoints
        }
        return ordering.sortedCopy(dataPoints)
    }

    fun widthPx(p: DataPointAesthetics, ctx: GeomContext, minWidth: Double): Double {
        val w = p.width()
        val width = w!! * ctx.getResolution(Aes.X)
        return max(width, minWidth)
    }

    fun withDefined(dataPoints: Iterable<DataPointAesthetics>, vararg required: Aes<*>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p ->
            var match = true
            for (aes in required) {
                if (!p.defined(aes)) {
                    match = false
                    break
                }
            }
            match
        }
    }

    fun rectangleByDataPoint(p: DataPointAesthetics, ctx: GeomContext): DoubleRectangle {
        val x = p.x()!!
        val y = p.y()!!
        val width = widthPx(p, ctx, 2.0)

        val origin: DoubleVector
        val dimensions: DoubleVector
        if (y >= 0) {
            origin = DoubleVector(x - width / 2, 0.0)
            dimensions = DoubleVector(width, y)
        } else {
            origin = DoubleVector(x - width / 2, y)
            dimensions = DoubleVector(width, -y)
        }

        return DoubleRectangle(origin, dimensions)
    }

    fun createGroups(dataPoints: Iterable<DataPointAesthetics>): Map<Int, List<DataPointAesthetics>> {
        val pointsByGroup = HashMap<Int, MutableList<DataPointAesthetics>>()
        for (p in dataPoints) {
            val group = p.group()!!
            if (!pointsByGroup.containsKey(group)) {
                pointsByGroup[group] = ArrayList()
            }
            pointsByGroup[group]!!.add(p)
        }

        return pointsByGroup
    }

    fun createMultipolygon(points: List<DoubleVector>): MultiPolygon {
        if (points.isEmpty()) {
            return MultiPolygon(emptyList())
        }

        val polygons = ArrayList<Polygon>()
        var rings: MutableList<Ring> = ArrayList<Ring>()

        for (ring in createRingsFromPoints(points)) {
            if (rings.isNotEmpty() && isClockwise(ring)) {
                polygons.add(Polygon(rings))
                rings = ArrayList<Ring>()
            }
            rings.add(Ring(ring.map { Point(it.x, it.y) }))
        }

        if (!rings.isEmpty()) {
            polygons.add(Polygon(rings))
        }

        return MultiPolygon(polygons)
    }

    private fun isClockwise(ring: List<DoubleVector>): Boolean {
        if (ring.isEmpty()) {
            throw IllegalStateException("Ring shouldn't be empty to calculate clockwise")
        }

        var sum = 0.0
        var prev = ring[ring.size - 1]
        for (point in ring) {
            sum += prev.x * point.y - point.x * prev.y
            prev = point
        }
        return sum < 0.0
    }

    fun createRingsFromPoints(points: List<DoubleVector>): List<List<DoubleVector>> {
        val ringIntervals = GeomUtil.findRingIntervals(points)

        val rings = ArrayList<List<DoubleVector>>(ringIntervals.size)
        ringIntervals.forEach { ringInterval -> rings.add(sublist(points, ringInterval)) }

        if (!rings.isEmpty()) {
            val lastRing = rings[rings.size - 1]
            if (!isClosed(lastRing)) {
                rings.removeAt(rings.size - 1)
                rings.add(makeClosed(lastRing))
            }
        }

        return rings
    }

    private fun makeClosed(path: List<DoubleVector>): List<DoubleVector> {
        val closedList = ArrayList(path)
        closedList.add(closedList[0])
        return closedList
    }

    private fun findRingIntervals(path: List<DoubleVector>): List<ClosedRange<Int>> {
        val intervals = ArrayList<ClosedRange<Int>>()
        var startIndex = 0

        var i = 0
        val n = path.size
        while (i < n) {
            if (startIndex != i && path[startIndex] == path[i]) {
                intervals.add(ClosedRange.closed(startIndex, i + 1))
                startIndex = i + 1
            }
            i++
        }

        if (startIndex != path.size) {
            intervals.add(ClosedRange.closed(startIndex, path.size))
        }
        return intervals
    }

    fun <T> sublist(list: List<T>, range: ClosedRange<Int>): List<T> {
        return list.subList(range.lowerEndpoint(), range.upperEndpoint())
    }

    fun rectToGeometry(minX: Double, minY: Double, maxX: Double, maxY: Double): List<DoubleVector> {
        return listOf(
                DoubleVector(minX, minY),
                DoubleVector(minX, maxY),
                DoubleVector(maxX, maxY),
                DoubleVector(maxX, minY),
                DoubleVector(minX, minY)
        )
    }
}