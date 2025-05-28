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
    EPOCH_MILLIS,
    DATE_MILLIS_UTC, // Local date.
    MIDNIGHT_MILLIS, // Local time.
}
