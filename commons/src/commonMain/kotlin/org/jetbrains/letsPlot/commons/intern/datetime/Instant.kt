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
        this.kotlinInstant = KotlinInstant.fromEpochMilliseconds(epochMillis)
    }

    internal constructor(kotlinInstant: KotlinInstant) {
        this.kotlinInstant = kotlinInstant
    }

    internal constructor(kotlinxInstant: kotlinx.datetime.Instant) : this(kotlinxInstant.toEpochMilliseconds())

    internal val kotlinInstant: KotlinInstant

    fun toEpochMilliseconds(): Long = kotlinInstant.toEpochMilliseconds()

    fun toDateTime(tz: TimeZone): DateTime {
        val kotlinxLocalDateTime = kotlinInstant.toLocalDateTime(tz.kotlinxTz)
        return DateTime(kotlinxLocalDateTime)
    }

//    fun daysUntil(other: Instant, tz: TimeZone): Int {
//        return this.kotlinxInstant.daysUntil(other.kotlinxInstant, tz.kotlinxTz)
//    }

    fun add(millis: Long): Instant {
        return Instant(kotlinInstant.toEpochMilliseconds() + millis)
    }

    fun add(duration: Duration): Instant {
        return Instant(kotlinInstant.toEpochMilliseconds() + duration.totalMillis)
    }

    override fun compareTo(other: Instant) = kotlinInstant.compareTo(other.kotlinInstant)
    override fun hashCode() = kotlinInstant.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Instant) return false
        return kotlinInstant == other.kotlinInstant
    }

    override fun toString() = kotlinInstant.toString()
}