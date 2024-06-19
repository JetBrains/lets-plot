/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

/**
 * Alignment of the plot title/subtitle and caption:
 *  - "panel" - titles/caption are aligned to the plot panels
 *  - "plot" -  titles/caption are aligned to the entire plot (excluding margins)
 */
enum class TitlePosition {
    PANEL,
    PLOT
}