/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.algorithms


class AdaptiveResampler<T>(
    private val transform: (T) -> T?,
    val delegate: PointDelegate<T>,
    precision: Double
) {
    private val precisionSqr: Double = precision * precision

    fun resample(points: List<T>): List<T> {
        val result = ArrayList<T>(points.size)

        for (i in 1.until(points.size)) {
            result.removeLastOrNull()

            resample(points[i - 1], points[i])
                .forEach(result::add)
        }

        return result
    }

    fun resample(p1: T, p2: T): List<T> {
        val result = ArrayList<T>()
        val temp = ArrayList<T>()
        result.add(p1)
        temp.add(p2)

        while (temp.isNotEmpty()) {
            when (val missingPoint = computeMissingPoint(result.last(), temp.last())) {
                null -> result.add(temp.removeLast())
                else -> temp.add(missingPoint)
            }
        }
        return result.mapNotNull { transform(it) }
    }

    private fun computeMissingPoint(p1: T, p2: T): T? {
        val pc = (p1 + p2) / 2.0
        val q1 = transform(p1) ?: return null
        val q2 = transform(p2) ?: return null
        val qc = transform(pc) ?: return null

        val distance = if (q1 == q2) {
            length(q1, qc)
        } else {
            distance(qc, q1, q2)
        }

        return if (distance < precisionSqr) null else pc
    }


    private fun length(p1: T, p2: T): Double {
        val x = p2.x - p1.x
        val y = p2.y - p1.y
        return x * x + y * y
    }

    private fun distance(p: T, l1: T, l2: T): Double {
        val ortX = l2.x - l1.x
        val ortY = -(l2.y - l1.y)

        val dot = (p.x - l1.x) * ortY + (p.y - l1.y) * ortX
        val len = ortY * ortY + ortX * ortX

        return dot * dot / len
    }

    inline val T.x get() = delegate.x(this)
    inline val T.y get() = delegate.y(this)
    private operator fun T.plus(other: T): T = delegate.newObj(x + other.x, y + other.y)
    private operator fun T.div(other: T): T = delegate.newObj(x / other.x, y / other.y)
    private operator fun T.div(v: Double): T = delegate.newObj(x / v, y / v)

    interface PointDelegate<T> {
        fun x(obj: T): Double
        fun y(obj: T): Double
        fun newObj(x: Double, y: Double): T
    }
}
