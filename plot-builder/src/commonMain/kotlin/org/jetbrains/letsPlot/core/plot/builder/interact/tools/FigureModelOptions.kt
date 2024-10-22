/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

object FigureModelOptions {
    // Composite figures: id of the target plot panel to apply options to.
    const val TARGET_ID = "target_id"

    // Tools can temporarily override default or provided limits.
    const val COORD_XLIM_TRANSFORMED = "coord_xlim_transformed"  // array of two nullable numbers
    const val COORD_YLIM_TRANSFORMED = "coord_ylim_transformed"
}