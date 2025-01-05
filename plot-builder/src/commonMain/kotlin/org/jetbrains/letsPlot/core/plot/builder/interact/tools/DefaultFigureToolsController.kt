/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

class DefaultFigureToolsController constructor(
    private val figure: FigureModel,
    private val errorMessageHandler: (String) -> Unit
) : FigureToolsController() {
    override fun activateFigureTool(tool: ToggleTool) {
        if (!tool.active) {
            figure.activateInteractions(
                origin = tool.name,
                interactionSpecList = tool.interactionSpecList
            )
        }
    }

    override fun deactivateFigureTool(tool: ToggleTool) {
        if (tool.active) {
            figure.deactivateInteractions(tool.name)
        }
    }

    override fun updateFigureView(specOverride: Map<String, Any>?) {
        figure.updateView(specOverride)
    }

    override fun showFigureError(msg: String) {
        errorMessageHandler(msg)
    }
}