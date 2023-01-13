/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.interval

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

    companion object {
        fun withLowerEnd(lowerEnd: Double, length: Double) = DoubleSpan(lowerEnd, lowerEnd + length)
        fun withUpperEnd(upperEnd: Double, length: Double) = DoubleSpan(upperEnd - length, upperEnd)
        fun singleton(v: Double) = DoubleSpan(v, v)

        fun encloseAll(values: Iterable<Double?>): DoubleSpan {
            val min = values.filterNotNull().minOrNull()
            val max = values.filterNotNull().maxOrNull()
            return if (min != null && max != null) {
                DoubleSpan(min, max)
            } else {
                throw NoSuchElementException("Can't create DoubleSpan: the input is empty or contains NULLs.")
            }
        }
    }
}