/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

import kotlin.math.max
import kotlin.math.min

class Interval(val lowerBound: Int, val upperBound: Int) {

    val length: Int
        get() = upperBound - lowerBound

    init {
        if (lowerBound > upperBound) {
            throw IllegalArgumentException("Lower bound is greater than upper: lower bound=" + lowerBound
                    + ", upper bound=" + upperBound)
        }
    }

    operator fun contains(point: Int): Boolean {
        return lowerBound <= point && point <= upperBound
    }

    operator fun contains(other: Interval): Boolean {
        return contains(other.lowerBound) && contains(other.upperBound)
    }

    fun intersects(other: Interval): Boolean {
        return contains(other.lowerBound) || other.contains(lowerBound)
    }

    /**
     * Returns minimal interval that contains both this and other intervals.
     */
    fun union(other: Interval): Interval {
        return Interval(min(lowerBound, other.lowerBound), max(upperBound, other.upperBound))
    }

    fun add(delta: Int): Interval {
        return Interval(lowerBound + delta, upperBound + delta)
    }

    fun sub(delta: Int): Interval {
        return Interval(lowerBound - delta, upperBound - delta)
    }

    override fun toString(): String {
        return "[$lowerBound, $upperBound]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Interval) return false

        val interval = other as Interval?
        return if (lowerBound != interval!!.lowerBound) false else upperBound == interval.upperBound
    }

    override fun hashCode(): Int {
        var result = lowerBound
        result = 31 * result + upperBound
        return result
    }
}