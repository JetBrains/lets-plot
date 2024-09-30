/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

data class ExponentFormat(
    val notationType: NotationType,
    val min: Int? = null,
    val max: Int? = null
) {
    enum class NotationType {
        E, POW, POW_FULL
    }
}