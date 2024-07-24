/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

class ToggleTool(
    val spec: Map<String, Any>
) {
    val name = spec.getValue("name") as String
    val label = spec.getValue("label") as String

    @Suppress("UNCHECKED_CAST")
    val interactionSpecList = spec.getValue("interactions") as List<Map<String, Any>>

    var active: Boolean = false
}
