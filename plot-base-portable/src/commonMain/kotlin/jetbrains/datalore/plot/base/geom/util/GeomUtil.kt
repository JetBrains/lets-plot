/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max


object GeomUtil {
    val TO_LOCATION_X_Y = { p: DataPointAesthetics ->
        toLocationOrNull(
            p.x(),
            p.y()
        )
    }
    val TO_LOCATION_X_YMIN = { p: DataPointAesthetics ->
        toLocationOrNull(
            p.x(),
            p.ymin()
        )
    }
    val TO_LOCATION_X_YMAX = { p: DataPointAesthetics ->
        toLocationOrNull(
            p.x(),
            p.ymax()
        )
    }
    val TO_LOCATION_X_ZERO = { p: DataPointAesthetics ->
        toLocationOrNull(
            p.x(),
            0.0
        )
    }
    val TO_RECTANGLE = { p: DataPointAesthetics ->
        if (SeriesUtil.allFinite(p.xmin(), p.ymin(), p.xmax(), p.ymax())) {
            rectToGeometry(
                p.xmin()!!,
                p.ymin()!!,
                p.xmax()!!,
                p.ymax()!!
            )
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
    private val WITH_X = { pointAes: DataPointAesthetics -> SeriesUtil.isFinite(pointAes.x()) }
    private val WITH_Y = { pointAes: DataPointAesthetics -> SeriesUtil.isFinite(pointAes.y()) }

    private fun toLocationOrNull(x: Double?, y: Double?): DoubleVector? {
        return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
            DoubleVector(x!!, y!!)
        } else null
    }

    @Suppress("FunctionName")
    fun with_X_Y(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> WITH_X_Y.invoke(p) }
    }

    @Suppress("FunctionName")
    fun with_X(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> WITH_X.invoke(p) }
    }

    @Suppress("FunctionName")
    fun with_Y(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> WITH_Y.invoke(p) }
    }

    @Suppress("FunctionName")
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

    fun withDefined(dataPoints: Iterable<DataPointAesthetics>, aes: Aes<*>): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> p.defined(aes) }
    }

    fun withDefined(
        dataPoints: Iterable<DataPointAesthetics>,
        aes0: Aes<*>,
        aes1: Aes<*>
    ): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> p.defined(aes0) && p.defined(aes1) }
    }

    fun withDefined(
        dataPoints: Iterable<DataPointAesthetics>,
        aes0: Aes<*>,
        aes1: Aes<*>,
        aes2: Aes<*>
    ): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> p.defined(aes0) && p.defined(aes1) && p.defined(aes2) }
    }

    fun withDefined(
        dataPoints: Iterable<DataPointAesthetics>,
        aes0: Aes<*>,
        aes1: Aes<*>,
        aes2: Aes<*>,
        aes3: Aes<*>
    ): Iterable<DataPointAesthetics> {
        return dataPoints.filter { p -> p.defined(aes0) && p.defined(aes1) && p.defined(aes2) && p.defined(aes3) }
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