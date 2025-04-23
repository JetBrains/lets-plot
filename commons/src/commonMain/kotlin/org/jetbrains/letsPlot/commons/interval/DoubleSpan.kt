/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.interval

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DoubleSpan(
    lower: Double,
    upper: Double
) : NumSpan() {
    override val lowerEnd: Double = min(lower, upper)
    override val upperEnd: Double = max(lower, upper)
    val length: Double = upperEnd - lowerEnd

    init {
        check(lower.isFinite() && upper.isFinite()) {
            "Ends must be finite: lower=$lower upper=$upper"
        }
    }

    operator fun contains(v: Double): Boolean {
        return v >= lowerEnd && v <= upperEnd
    }

    operator fun contains(other: DoubleSpan): Boolean {
        return lowerEnd <= other.lowerEnd && upperEnd >= other.upperEnd
    }

    fun encloses(other: DoubleSpan): Boolean {
        return lowerEnd <= other.lowerEnd && upperEnd >= other.upperEnd
    }

    fun connected(other: DoubleSpan): Boolean {
        return !(lowerEnd > other.upperEnd || upperEnd < other.lowerEnd)
    }

    fun union(other: DoubleSpan): DoubleSpan {
        if (encloses(other)) return this
        return if (other.encloses(this)) {
            other
        } else {
            DoubleSpan(
                min(lowerEnd, other.lowerEnd),
                max(upperEnd, other.upperEnd)
            )
        }
    }

    fun intersection(other: DoubleSpan): DoubleSpan {
        if (!connected(other)) throw IllegalArgumentException("Ranges are not connected: this=$this other=$other")
        if (encloses(other)) return other
        return if (other.encloses(this)) {
            this
        } else {
            DoubleSpan(
                max(lowerEnd, other.lowerEnd),
                min(upperEnd, other.upperEnd)
            )
        }
    }

    fun expanded(expand: Double): DoubleSpan {
        @Suppress("NAME_SHADOWING")
        val expand = if (expand >= 0.0) {
            expand
        } else {
            -(min(this.length / 2, abs(expand)))
        }
        return DoubleSpan(lowerEnd - expand, upperEnd + expand)
    }

    fun toPair(): Pair<Double, Double> {
        return lowerEnd to upperEnd
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as DoubleSpan

        if (lowerEnd != other.lowerEnd) return false
        if (upperEnd != other.upperEnd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + lowerEnd.hashCode()
        result = 31 * result + upperEnd.hashCode()
        return result
    }


    companion object {
        fun withLowerEnd(lowerEnd: Double, length: Double) = DoubleSpan(lowerEnd, lowerEnd + length)
        fun withUpperEnd(upperEnd: Double, length: Double) = DoubleSpan(upperEnd - length, upperEnd)
        fun singleton(v: Double) = DoubleSpan(v, v)

        fun encloseAllQ(values: Iterable<Double?>): DoubleSpan? {
            val (min, max) = values.filterNotNull().filter { it.isFinite() }.let {
                Pair(it.minOrNull(), it.maxOrNull())
            }
            return if (min == null || max == null) {
                null
            } else {
                DoubleSpan(min, max)
            }
        }

        fun encloseAll(values: Iterable<Double?>): DoubleSpan {
            return encloseAllQ(values.toList())
                ?: throw NoSuchElementException("Can't create DoubleSpan: the input is empty or contains NULLs.")
        }
    }
}