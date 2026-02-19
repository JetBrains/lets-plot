/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import java.awt.Dimension
import javax.swing.JComponent

interface PlotComponentProvider {
    @Deprecated("Use overload with SpecOverrideState", replaceWith = ReplaceWith("createComponent(containerSize, sizingPolicy, SpecOverrideState(specOverrideList, null))"))
    fun createComponent(
        containerSize: Dimension?,
        sizingPolicy: SizingPolicy,
        specOverrideList: List<Map<String, Any>> = emptyList()
    ): JComponent {
        return createComponent(containerSize, sizingPolicy, SpecOverrideState(specOverrideList, null))
    }

    fun createComponent(
        containerSize: Dimension?,
        sizingPolicy: SizingPolicy,
        specOverrideState: SpecOverrideState
    ): JComponent

    @Deprecated("Removed API", level = DeprecationLevel.ERROR)
    fun getPreferredSize(containerSize: Dimension): Dimension =
        throw IllegalStateException("Removed API: 'fun getPreferredSize(containerSize: Dimension)'")
}