/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util.sizing

// See: `SizingMode`
object SizingOption {
    const val KEY = "sizing"

    const val WIDTH_MODE = "width_mode"     // fit | min | scaled | fixed
    const val HEIGHT_MODE = "height_mode"   // fit | min | scaled | fixed

    const val WIDTH = "width"
    const val HEIGHT = "height"
}