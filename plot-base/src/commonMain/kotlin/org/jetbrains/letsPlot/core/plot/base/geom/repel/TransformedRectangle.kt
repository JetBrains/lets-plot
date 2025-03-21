/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.repel

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleVectorExtensions.dot
import kotlin.math.sqrt


class TransformedRectangle(
    private val lt: DoubleVector,
    private val lb: DoubleVector,
    private val rb: DoubleVector,
    private val rt: DoubleVector
) {
    constructor(list: List<DoubleVector>): this(list[0], list[1], list[2], list[3])

    val width: Double
        get() = rt.subtract(lt).length()

    val height: Double
        get() = lt.subtract(lb).length()

    val hypot: Double
        get() = sqrt(width * width + height * height)

    val center: DoubleVector
        get() = DoubleVector((lt.x + rb.x) / 2, (lt.y + rb.y) / 2)

    val bbox: DoubleRectangle
        get() {
            val left = minOf(lt.x, lb.x, rb.x, rt.x)
            val right = maxOf(lt.x, lb.x, rb.x, rt.x)
            val top = minOf(lt.y, lb.y, rb.y, rt.y)
            val bottom = maxOf(lt.y, lb.y, rb.y, rt.y)
            return DoubleRectangle(DoubleVector(left, top), DoubleVector(right - left, bottom - top))
        }

    val edges: List<DoubleSegment>
        get() = listOf(
            DoubleSegment(lt, lb),
            DoubleSegment(lb, rb),
            DoubleSegment(rb, rt),
            DoubleSegment(rt, lt)
        )

    fun add(v: DoubleVector): TransformedRectangle {
        return TransformedRectangle(lt.add(v), lb.add(v), rb.add(v), rt.add(v))
    }

    fun expand(padding: Double): TransformedRectangle {
        val shiftUp = lt.subtract(lb).normalize().mul(padding)
        val shiftRight = rt.subtract(lt).normalize().mul(padding)

        return TransformedRectangle(
            lt.add(shiftUp).add(shiftRight.mul(-1.0)),
            lb.add(shiftUp.mul(-1.0)).add(shiftRight.mul(-1.0)),
            rb.add(shiftUp.mul(-1.0)).add(shiftRight),
            rt.add(shiftUp).add(shiftRight))
    }

    fun anchor(hjust: Double, vjust: Double): DoubleVector {
        val horizontal = rb.subtract(lb)
        val vertical = lt.subtract(lb)
        val hv = horizontal.mul(hjust)
        val vv = vertical.mul(vjust)

        return lb.add(hv).add(vv)
    }

    fun findEdgeConnectionPoint(point: DoubleVector, hjust: Double, vjust: Double): DoubleVector? {
        val s1 = DoubleSegment(point, anchor(hjust, vjust))

        for (edge in edges) {
            if (s1.intersection(edge) != null)
                return DoubleVector(
                    (edge.start.x + edge.end.x) / 2,
                    (edge.start.y + edge.end.y) / 2
                )
        }

        return null
    }

    fun intersects(circle: DoubleCircle): Boolean {
        if (pointInRectangle(circle.center)) return true

        for (edge in edges) {
            if (circle.intersects(edge)) return true
        }

        return false
    }

    fun intersects(other: TransformedRectangle): Boolean {
        val axes = getSeparatingAxes() + other.getSeparatingAxes()

        for (axis in axes) {
            val projectionA = projectOntoAxis(axis)
            val projectionB = other.projectOntoAxis(axis)

            if (!overlaps(projectionA, projectionB)) {
                return false
            }
        }

        return true
    }

    fun pointInRectangle(point: DoubleVector): Boolean {
        val ab = lb.subtract(lt)
        val ad = rt.subtract(lt)
        val ap = point.subtract(lt)

        val abDotAp = ab.dot(ap)
        val adDotAp = ad.dot(ap)

        return 0 <= abDotAp && abDotAp <= ab.dot(ab) && 0 <= adDotAp && adDotAp <= ad.dot(ad)
    }

    private fun getSeparatingAxes(): List<DoubleVector> {
        return listOf(
            rt.subtract(lt).normal(),
            rb.subtract(rt).normal(),
            lb.subtract(rb).normal(),
            lt.subtract(lb).normal()
        )
    }

    private fun projectOntoAxis(axis: DoubleVector): Pair<Double, Double> {
        val projections = listOf(
            lt.dot(axis),
            lb.dot(axis),
            rb.dot(axis),
            rt.dot(axis)
        )

        return projections.minOrNull()!! to projections.maxOrNull()!!
    }

    private fun overlaps(projA: Pair<Double, Double>, projB: Pair<Double, Double>): Boolean {
        return projA.second >= projB.first && projB.second >= projA.first
    }

    private fun DoubleVector.normal(): DoubleVector {
        return DoubleVector(-y, x).savedNormalize()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TransformedRectangle

        if (lt != other.lt) return false
        if (lb != other.lb) return false
        if (rb != other.rb) return false
        if (rt != other.rt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lt.hashCode()
        result = 31 * result + lb.hashCode()
        result = 31 * result + rb.hashCode()
        result = 31 * result + rt.hashCode()
        return result
    }

    override fun toString(): String {
        return "TransformedRectangle(lt=$lt, lb=$lb, rb=$rb, rt=$rt)"
    }

    companion object {
        val ZERO = TransformedRectangle(DoubleVector.ZERO, DoubleVector.ZERO, DoubleVector.ZERO, DoubleVector.ZERO)

        fun DoubleVector.savedNormalize(): DoubleVector {
            val length = this.length()
            return if (length == 0.0) {
                DoubleVector.ZERO
            } else {
                this.mul(1.0 / length)
            }
        }
    }
}