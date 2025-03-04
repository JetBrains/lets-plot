/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

open class DefaultPlotPanelJfx(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = DefaultPlotComponentProviderJfx(
        processedSpec = processedSpec,
        executor = DefaultSwingContextJfx.JFX_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio),
    repaintDelay = repaintDelay,
    applicationContext = DefaultSwingContextJfx()
)
