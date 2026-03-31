/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package tools

import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool

internal class FigureToolsControllerJs(
    private val figure: () -> FigureModel?
) : FigureToolsController() {
    override fun activateFigureTool(tool: ToggleTool) {
        if (!tool.active) {
            figure()?.activateInteractions(
                origin = tool.name,
                interactionSpecList = tool.interactionSpecList
            ) ?: LOG.info { "The tools controller is unbound." }
        }
    }

    override fun deactivateFigureTool(tool: ToggleTool) {
        if (tool.active) {
            figure()?.deactivateInteractions(tool.name)
                ?: LOG.info { "The tools controller is unbound." }
        }
    }

    override fun updateFigureView(specOverride: Map<String, Any>?) {
        figure()?.updateView(specOverride)
            ?: LOG.info { "The tools controller is unbound." }
    }

    override fun showFigureError(msg: String) {
        window.alert(msg)
    }

    companion object {
        private val LOG = PortableLogging.logger("FigureToolsControllerJs")
    }
}
