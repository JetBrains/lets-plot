/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.TimeZone as KotlinxTimeZone

class TimeZone {
    /**
     * Creates a TimeZone instance from a timezone ID.
     *
     * @param id The timezone ID (e.g., "UTC", "America/New_York", "Europe/London")
     * @throws IllegalArgumentException if the timezone ID is not recognized
     */
    constructor(id: String) {
        try {
            this.kotlinxTz = KotlinxTimeZone.of(id)
        } catch (e: IllegalTimeZoneException) {
            throw IllegalArgumentException("Unsupported time zone: '$id'", e)

            // Note:
            // When running on Wasm WASI, you can only use fixed-offset time zones (like UTC+2) by default.
            // To use named time zones with daylight saving time rules (like "Europe/Berlin"),
            // you must add dependency: kotlinx-datetime-zoneinfo.
        }
    }

    internal constructor(kotlinxTimeZone: KotlinxTimeZone) {
        this.kotlinxTz = kotlinxTimeZone
    }

    internal val kotlinxTz: KotlinxTimeZone

    val id: String get() = kotlinxTz.id

    override fun toString() = kotlinxTz.toString()

    companion object {
        init {
            TimeZoneInitializer.initialize()
        }

        val UTC by lazy { TimeZone(KotlinxTimeZone.UTC) }
    }
}