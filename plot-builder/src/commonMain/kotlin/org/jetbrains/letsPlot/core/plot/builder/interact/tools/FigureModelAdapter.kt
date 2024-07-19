/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

interface FigureModelAdapter {
    fun activateTool(tool: ToggleTool)
    fun deactivateTool(tool: ToggleTool)
    fun updateView(specOverride: Map<String, Any>? = null)
}