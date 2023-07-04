/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.math.abs
import kotlin.math.min

class DoubleSegment(val start: DoubleVector, val end: DoubleVector) {

    fun distance(v: DoubleVector): Double {
        val vs = start.subtract(v)
        val ve = end.subtract(v)

        if (isDistanceToLineBest(v)) {
            val pVolume = abs(vs.x * ve.y - vs.y * ve.x)
            return pVolume / length()
        } else {
            return min(vs.length(), ve.length())
        }
    }

    private fun isDistanceToLineBest(v: DoubleVector): Boolean {
        val es = start.subtract(end)
        val se = es.negate()
        val ev = v.subtract(end)
        val sv = v.subtract(start)

        return es.dotProduct(ev) >= 0 && se.dotProduct(sv) >= 0
    }

    fun intersection(with: DoubleSegment): DoubleVector? {
        val o1 = start
        val o2 = with.start
        val d1 = end.subtract(start)
        val d2 = with.end.subtract(with.start)

        val td = d1.dotProduct(d2.orthogonal())
        if (td == 0.0) {
            return null
        }
        val t = o2.subtract(o1).dotProduct(d2.orthogonal()) / td
        if (t < 0 || t > 1) {
            return null
        }

        val sd = d2.dotProduct(d1.orthogonal())
        val s = o1.subtract(o2).dotProduct(d1.orthogonal()) / sd
        return if (s < 0 || s > 1) {
            null
        } else o1.add(d1.mul(t))

    }

    fun length(): Double {
        return start.subtract(end).length()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleSegment) {
            return false
        }

        val l = other as DoubleSegment?
        return l!!.start.equals(start) && l.end.equals(end)
    }

    override fun hashCode(): Int {
        return start.hashCode() * 31 + end.hashCode()
    }

    override fun toString(): String {
        return "[$start -> $end]"
    }
}