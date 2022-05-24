/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.interval

import kotlin.math.max
import kotlin.math.min

class IntSpan(
    lower: Int,
    upper: Int
) : NumSpan() {
    override val lowerEnd: Int = min(lower, upper)
    override val upperEnd: Int = max(lower, upper)
    val length: Int = upperEnd - lowerEnd

    operator fun contains(v: Int): Boolean {
        return v >= lowerEnd && v <= upperEnd
    }

    fun encloses(other: IntSpan): Boolean {
        return lowerEnd <= other.lowerEnd && upperEnd >= other.upperEnd
    }

    fun connected(other: IntSpan): Boolean {
        return !(lowerEnd > other.upperEnd || upperEnd < other.lowerEnd)
    }

    fun union(other: IntSpan): IntSpan {
        if (encloses(other)) return this
        return if (other.encloses(this)) {
            other
        } else {
            IntSpan(
                min(lowerEnd, other.lowerEnd),
                max(upperEnd, other.upperEnd)
            )
        }
    }

    fun intersection(other: IntSpan): IntSpan {
        if (!connected(other)) throw IllegalArgumentException("Ranges are not connected: this=$this other=$other")
        if (encloses(other)) return other
        return if (other.encloses(this)) {
            this
        } else {
            IntSpan(
                max(lowerEnd, other.lowerEnd),
                min(upperEnd, other.upperEnd)
            )
        }
    }
}