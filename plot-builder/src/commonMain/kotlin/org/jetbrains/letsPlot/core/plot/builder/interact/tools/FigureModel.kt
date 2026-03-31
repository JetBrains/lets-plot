/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec

// See: PlotPanel.ResizeHook
interface FigureModel {
    fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration
    fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>)
    fun deactivateInteractions(origin: String)
    fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>)
    fun updateView(specOverride: Map<String, Any>? = null)
    fun addDisposible(disposable: Disposable)
    fun dispose()
}