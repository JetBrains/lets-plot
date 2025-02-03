/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

open class DefaultPlotPanelBatik(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = DefaultPlotComponentProviderBatik(
        processedSpec = processedSpec,
        executor = DefaultSwingContextBatik.AWT_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio),
    preferredSizeFromPlot = preferredSizeFromPlot,
    repaintDelay = repaintDelay,
    applicationContext = DefaultSwingContextBatik()
)