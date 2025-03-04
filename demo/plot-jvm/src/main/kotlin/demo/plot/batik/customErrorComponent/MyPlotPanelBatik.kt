/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.customErrorComponent

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

open class MyPlotPanelBatik(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = MyPlotComponentProviderBatik(
        processedSpec = processedSpec,
        executor = MY_AWT_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio),
    repaintDelay = repaintDelay,
    applicationContext = MY_APP_CONTEXT
)