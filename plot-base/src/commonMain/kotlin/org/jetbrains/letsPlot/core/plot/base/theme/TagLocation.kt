/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

/**
 * Location of the plot tag:
    'plot'   - the tag is positioned relative to the entire plot area without affecting layout.
    'panel'  - the tag is positioned relative to the panel (data) area without affecting layout.
    'margin' - the tag is placed in the plot margin area. Space for the tag is reserved by the layout,
so other plot elements are shifted to avoid overlap. */
enum class TagLocation {
    PLOT,
    PANEL,
    MARGIN
}

