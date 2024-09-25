/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat

enum class ExponentFormat(
    val value: NumberFormat.ExponentFormat,
) {
    E(NumberFormat.ExponentFormat.E),
    POW(NumberFormat.ExponentFormat.POW),
    POW_FULL(NumberFormat.ExponentFormat.POW_FULL)
}