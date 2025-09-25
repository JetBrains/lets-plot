package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

class DefaultPlotPanelCanvas(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = DefaultPlotComponentProviderCanvas(
        processedSpec = processedSpec,
        executor = DefaultSwingContextCanvas.AWT_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio),
    repaintDelay = repaintDelay,
    applicationContext = DefaultSwingContextCanvas(),
    showToolbar = processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)
)
