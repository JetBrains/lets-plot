/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotViewerWindowBase
import java.awt.Dimension

class PlotViewerWindowJfx(
    title: String,
    private val rawSpec: MutableMap<String, Any>,
    private val windowSize: Dimension? = null,
    private val preserveAspectRatio: Boolean = false,
    private val refreshRate: Int = 300,  // ms
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
    refreshRate = refreshRate,
    applicationContext = DefaultSwingContextJfx()
) {

    override fun createPlotComponentProvider(
        computationMessagesHandler: (List<String>) -> Unit
    ): PlotComponentProvider {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        return DefaultPlotComponentProviderJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRatio,
            executor = DefaultSwingContextJfx.JFX_EDT_EXECUTOR,
            computationMessagesHandler = computationMessagesHandler
        )
    }
}