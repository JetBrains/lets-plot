/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("OPT_IN_USAGE")

package tools

import FigureModelJs
import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ

internal class FigureToolsControllerJs(
    private val figure: () -> FigureModelJs?
) : FigureToolsController() {
    override fun activateFigureTool(tool: ToggleTool) {
        if (!tool.active) {
            figure()?.activateInteractions(
                origin = tool.name,
                interactionSpecListJs = dynamicFromAnyQ(
                    tool.interactionSpecList.map { it.toMap() }
                )
            ) ?: LOG.info { "The tools controller is unbound." }
        }
    }

    override fun deactivateFigureTool(tool: ToggleTool) {
        if (tool.active) {
            figure()?.deactivateInteractions(tool.name)
                ?: LOG.info { "The tools controller is unbound." }
        }
    }

    override fun updateSpecOverride(specOverride: Map<String, Any>?) {
        figure()?.updateSpecOverride(specOverride)
            ?: LOG.info { "The tools controller is unbound." }
    }

    override fun updateFigureView() {
        figure()?.updateView()
            ?: LOG.info { "The tools controller is unbound." }
    }

    override fun showFigureError(msg: String) {
        window.alert(msg)
    }

    companion object {
        private val LOG = PortableLogging.logger("FigureToolsControllerJs")
    }
}