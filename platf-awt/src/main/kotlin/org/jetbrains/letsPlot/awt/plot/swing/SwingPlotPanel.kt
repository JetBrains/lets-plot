/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.swing

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

class SwingPlotPanel(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = SwingPlotComponentProvider(
        processedSpec = processedSpec,
        executor = SwingAppContext.AWT_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    sizingPolicy = SizingPolicy.Companion.fitContainerSize(preserveAspectRatio),
    repaintDelay = repaintDelay,
    applicationContext = SwingAppContext(),
    showToolbar = processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)
)