/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

internal object TZs {
    const val MOSCOW = "Europe/Moscow"
    const val BERLIN = "Europe/Berlin"
    const val PARIS = "Europe/Paris"
    const val NEW_YORK = "America/New_York"
    const val LONDON = "Europe/London"
    const val TOKYO = "Asia/Tokyo"
    const val SYDNEY = "Australia/Sydney"
    const val HONG_KONG = "Asia/Hong_Kong"
    const val SINGAPORE = "Asia/Singapore"

    val moscow: TimeZone get() = TimeZone(MOSCOW)
    val berlin: TimeZone get() = TimeZone(BERLIN)
    val newYork: TimeZone get() = TimeZone(NEW_YORK)
}