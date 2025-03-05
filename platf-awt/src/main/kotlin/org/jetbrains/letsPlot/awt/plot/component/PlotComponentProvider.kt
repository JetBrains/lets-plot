/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import java.awt.Dimension
import javax.swing.JComponent

interface PlotComponentProvider {
    fun createComponent(
        containerSize: Dimension?,
        sizingPolicy: SizingPolicy,
        specOverrideList: List<Map<String, Any>> = emptyList()
    ): JComponent

    @Deprecated("Removed API", level = DeprecationLevel.ERROR)
    fun getPreferredSize(containerSize: Dimension): Dimension =
        throw IllegalStateException("Removed API: 'fun getPreferredSize(containerSize: Dimension)'")
}