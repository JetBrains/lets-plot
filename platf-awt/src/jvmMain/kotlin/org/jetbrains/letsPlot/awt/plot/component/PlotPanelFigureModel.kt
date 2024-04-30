/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.FigureModel
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import java.awt.Dimension
import javax.swing.JComponent

// See: PlotPanel.ResizeHook
internal class PlotPanelFigureModel(
    private val plotPanel: PlotPanel,
    private val plotPreferredSize: (Dimension) -> Dimension,
    private val plotComponentFactory: (Dimension) -> JComponent,
    private val applicationContext: ApplicationContext,
) : FigureModel {

    private var toolEventHandler: ((Map<String, Any>) -> Unit)? = null
    private val activeInteractionsByOrigin: MutableMap<String, MutableList<Map<String, Any>>> = HashMap()

    var toolEventDispatcher: ToolEventDispatcher? = null
        set(value) {
            // ToDo: deactivate, then re-activate current interactions?
            field = value
        }

    var lastProvidedComponent: JComponent? = null

    override fun onToolEvent(callback: (Map<String, Any>) -> Unit) {
        toolEventHandler = callback
    }

    override fun activateInteraction(origin: String, interactionSpec: Map<String, Any>) {
        val responce: Map<String, Any> = toolEventDispatcher?.activateInteraction(origin, interactionSpec)
            ?: return
        if (responce.getValue(EVENT_NAME) == INTERACTION_ACTIVATED) {
            activeInteractionsByOrigin.getOrPut(origin) { ArrayList<Map<String, Any>>() }.add(interactionSpec)
        }
        toolEventHandler?.invoke(responce)
    }

    override fun deactivateInteractions(origin: String) {
        activeInteractionsByOrigin.remove(origin)?.forEach { interactionSpec ->
            val interactionName = interactionSpec.getValue(ToolInteractionSpec.NAME) as String
            val responce: Map<String, Any>? = toolEventDispatcher?.deactivateInteraction(origin, interactionName)
            if (responce != null) {
                toolEventHandler?.invoke(responce)
            }
        }
    }
}