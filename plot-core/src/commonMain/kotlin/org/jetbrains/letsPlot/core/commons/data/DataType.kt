/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

enum class DataType {
    NUMBER,
    STRING,
    INSTANT,

    /**
     * INSTANT_OF_DAY and INSTANT_OF_MONTH are INSTANTS formatted as year-month-day or year-month
     */
    INSTANT_OF_DAY,
    INSTANT_OF_MONTH,
    INSTANT_OF_QUARTER,
    INSTANT_OF_HALF_YEAR,
    INSTANT_OF_YEAR;


    val isTime: Boolean
        get() = this == INSTANT || isTimeInterval

    val isTimeInterval: Boolean
        get() = (this == INSTANT_OF_DAY
                || this == INSTANT_OF_MONTH
                || this == INSTANT_OF_QUARTER
                || this == INSTANT_OF_HALF_YEAR
                || this == INSTANT_OF_YEAR)

    val isString: Boolean
        get() = this == STRING

    val isNumber: Boolean
        get() = this == NUMBER
}
