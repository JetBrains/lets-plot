/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.distance2ToLine

// Note that resampled points may contain duplicates, i.e. rings detection may fail.
class AdaptiveResampler<T> private constructor(
    private val transform: (T) -> T?,
    precision: Double,
    private val dataAdapter: DataAdapter<T>
) {
    private val precisionSqr: Double = precision * precision

    companion object {
        const val PIXEL_PRECISION = 0.95
        private const val MAX_DEPTH_LIMIT = 9 // 1_025 points maximum (2^(LIMIT + 1) + 1)

        private val DOUBLE_VECTOR_ADAPTER = object : DataAdapter<DoubleVector> {
            override fun x(p: DoubleVector) = p.x
            override fun y(p: DoubleVector) = p.y
            override fun create(x: Double, y: Double) = DoubleVector(x, y)
        }

        private fun forDoubleVector(
            transform: (DoubleVector) -> DoubleVector?,
            precision: Double
        ): AdaptiveResampler<DoubleVector> {
            return AdaptiveResampler(transform, precision, DOUBLE_VECTOR_ADAPTER)
        }

        fun <T> generic(transform: (T) -> T?, precision: Double, adapter: DataAdapter<T>): AdaptiveResampler<T> {
            return AdaptiveResampler(transform, precision, adapter)
        }

        fun resample(
            points: List<DoubleVector>,
            precision: Double,
            transform: (DoubleVector) -> DoubleVector?
        ): List<DoubleVector> {
            return forDoubleVector(transform, precision).resample(points)
        }

        fun resample(
            p1: DoubleVector,
            p2: DoubleVector,
            precision: Double,
            transform: (DoubleVector) -> DoubleVector?
        ): List<DoubleVector> {
            return forDoubleVector(transform, precision).resample(p1, p2)
        }
    }

    fun resample(points: List<T>): List<T> {
        var pPrev: T = points.firstOrNull() ?: return emptyList()
        var tPrev: T = transform(pPrev) ?: return emptyList()

        val pLast = points.lastOrNull() ?: return emptyList()
        val tLast = transform(pLast) ?: return emptyList()

        val output = ArrayList<T>(points.size)
        points
            .asSequence()
            .drop(1)
            .forEach { p ->
                val t = transform(p) ?: return@forEach
                output.add(tPrev)
                resample(pPrev, tPrev, p, t, output, MAX_DEPTH_LIMIT)

                pPrev = p
                tPrev = t
            }

        output.add(tLast)

        return output
    }

    fun resample(p1: T, p2: T): List<T> {
        val t1 = transform(p1) ?: return emptyList()
        val t2 = transform(p2) ?: return emptyList()

        val output = mutableListOf<T>()
        output.add(t1)
        resample(p1, t1, p2, t2, output, MAX_DEPTH_LIMIT)
        output.add(t2)
        return output
    }

    private fun resample(p1: T, t1: T, p2: T, t2: T, output: MutableList<T>, depth: Int) {
        if (p1 == p2) {
            return
        }

        val pm = (p1 + p2) / 2.0
        val tm = transform(pm) ?: return

        if (distance2ToLine(tm.x, tm.y, t1.x, t1.y, t2.x, t2.y) >= precisionSqr && depth > 0) {
            resample(p1, t1, pm, tm, output, depth - 1)
            output.add(tm)
            resample(pm, tm, p2, t2, output, depth - 1)
        } else {
            if (distance2(t1.x, t1.y, t2.x, t2.y) > precisionSqr) {
                output.add(tm)
            }
        }
    }

    val T.x get() = dataAdapter.x(this)
    val T.y get() = dataAdapter.y(this)
    private operator fun T.minus(other: T): T = dataAdapter.create(x - other.x, y - other.y)
    private operator fun T.plus(other: T): T = dataAdapter.create(x + other.x, y + other.y)
    private operator fun T.div(other: T): T = dataAdapter.create(x / other.x, y / other.y)
    private operator fun T.div(v: Double): T = dataAdapter.create(x / v, y / v)

    interface DataAdapter<T> {
        fun x(p: T): Double
        fun y(p: T): Double
        fun create(x: Double, y: Double): T

        val T.x get() = x(this)
        val T.y get() = y(this)
    }
}
