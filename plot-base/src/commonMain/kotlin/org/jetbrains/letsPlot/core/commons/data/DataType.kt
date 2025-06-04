/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

enum class DataType {
    UNKNOWN,
    INTEGER,
    FLOATING,
    STRING,
    BOOLEAN,
    DATETIME_MILLIS,
    DATE_MILLIS, // Local date.
    TIME_MILLIS; // Local time.

    fun isTemporal(): Boolean {
        return this == DATETIME_MILLIS || this == DATE_MILLIS || this == TIME_MILLIS
    }
}
