/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.math.abs
import kotlin.math.min

class Segment(val start: Vector, val end: Vector) {

    fun distance(v: Vector): Double {
        val vs = start.sub(v)
        val ve = end.sub(v)

        if (isDistanceToLineBest(v)) {
            val pVolume = abs(vs.x * ve.y - vs.y * ve.x).toDouble()
            return pVolume / length()
        } else {
            return min(vs.toDoubleVector().length(), ve.toDoubleVector().length())
        }
    }

    private fun isDistanceToLineBest(v: Vector): Boolean {
        val es = start.sub(end)
        val se = es.negate()
        val ev = v.sub(end)
        val sv = v.sub(start)

        return es.dotProduct(ev) >= 0 && se.dotProduct(sv) >= 0
    }

    fun toDoubleSegment(): DoubleSegment {
        return DoubleSegment(start.toDoubleVector(), end.toDoubleVector())
    }

    fun intersection(with: Segment): DoubleVector? {
        return toDoubleSegment().intersection(with.toDoubleSegment())
    }

    fun length(): Double {
        return start.sub(end).length()
    }

    operator fun contains(v: Vector): Boolean {
        val p1 = v.sub(start)
        val p2 = v.sub(end)
        return if (p1.isParallel(p2)) {
            p1.dotProduct(p2) <= 0
        } else false
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Segment) {
            return false
        }

        val l = other as Segment?
        return l!!.start == start && l.end == end
    }

    override fun hashCode(): Int {
        return start.hashCode() * 31 + end.hashCode()
    }

    override fun toString(): String {
        return "[$start -> $end]"
    }
}