/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

enum class WeekDay(private val displayName: String, val isWeekend: Boolean) {
    MONDAY("MO", false),
    TUESDAY("TU", false),
    WEDNESDAY("WE", false),
    THURSDAY("TH", false),
    FRIDAY("FR", false),
    SATURDAY("SA", true),
    SUNDAY("SU", true);

    override fun toString(): String {
        return displayName
    }
}
