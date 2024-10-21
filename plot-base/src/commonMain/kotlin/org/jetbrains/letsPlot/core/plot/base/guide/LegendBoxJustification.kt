/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.guide

/**
 * justification of each legend within the overall bounding box, when there are multiple legends
 * ("left", "right", "top", "bottom", "center")
 */
enum class LegendBoxJustification {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    CENTER,
    AUTO
}