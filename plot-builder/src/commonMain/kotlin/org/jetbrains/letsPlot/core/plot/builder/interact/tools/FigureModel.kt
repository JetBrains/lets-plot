/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

// See: PlotPanel.ResizeHook
interface FigureModel {
    fun onToolEvent(callback: (Map<String, Any>) -> Unit)
    fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>)
    fun deactivateInteractions(origin: String)
    fun updateView(specOverride: Map<String, Any>? = null)
}