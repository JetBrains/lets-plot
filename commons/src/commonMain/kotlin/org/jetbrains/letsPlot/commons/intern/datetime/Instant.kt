/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant as KotlinInstant

/**
 * Represents specific points in time as milliseconds from the epoch and is timezone-agnostic.
 */
class Instant : Comparable<Instant> {
    constructor(epochMillis: Long) {
        this.kotlinxInstant = KotlinInstant.fromEpochMilliseconds(epochMillis)
    }

    internal constructor(kotlinxInstant: KotlinInstant) {
        this.kotlinxInstant = kotlinxInstant
    }

    internal val kotlinxInstant: KotlinInstant

    fun toEpochMilliseconds(): Long = kotlinxInstant.toEpochMilliseconds()

    fun toDateTime(tz: TimeZone): DateTime {
        val kotlinxLocalDateTime = kotlinxInstant.toLocalDateTime(tz.kotlinxTz)
        return DateTime(kotlinxLocalDateTime)
    }

//    fun daysUntil(other: Instant, tz: TimeZone): Int {
//        return this.kotlinxInstant.daysUntil(other.kotlinxInstant, tz.kotlinxTz)
//    }

    fun add(millis: Long): Instant {
        return Instant(kotlinxInstant.toEpochMilliseconds() + millis)
    }

    fun add(duration: Duration): Instant {
        return Instant(kotlinxInstant.toEpochMilliseconds() + duration.totalMillis)
    }

    override fun compareTo(other: Instant) = kotlinxInstant.compareTo(other.kotlinxInstant)
    override fun hashCode() = kotlinxInstant.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Instant) return false
        return kotlinxInstant == other.kotlinxInstant
    }

    override fun toString() = kotlinxInstant.toString()
}