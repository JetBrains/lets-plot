/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

//class Instant @JvmOverloads constructor(val timeSinceEpoch: Long = System.currentTimeMillis()) : Comparable<Instant> {
class Instant(val timeSinceEpoch: Long) : Comparable<Instant> {

    fun add(duration: Duration): Instant {
        return Instant(timeSinceEpoch + duration.duration)
    }

    fun sub(duration: Duration): Instant {
        return Instant(timeSinceEpoch - duration.duration)
    }

    fun to(instant: Instant): Duration {
        return Duration(instant.timeSinceEpoch - timeSinceEpoch)
    }

    override fun compareTo(other: Instant): Int {
        val delta = timeSinceEpoch - other.timeSinceEpoch
        return if (delta > 0) {
            1
        } else if (delta == 0L) {
            0
        } else {
            -1
        }
    }

    override fun hashCode(): Int {
        return timeSinceEpoch.toInt()
    }

    override fun toString(): String {
        return "" + timeSinceEpoch
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Instant) false else timeSinceEpoch == other.timeSinceEpoch

    }
}
