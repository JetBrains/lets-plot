/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

// See: PlotPanel.ResizeHook
interface FigureModel {
    fun onToolEvent(callback: (Map<String, Any>) -> Unit)
    fun activateInteraction(origin: String, interactionSpec: Map<String, Any>)
    fun deactivateInteractions(origin: String)
}