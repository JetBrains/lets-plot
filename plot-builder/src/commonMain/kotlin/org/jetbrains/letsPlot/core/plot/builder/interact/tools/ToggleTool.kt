/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.core.interact.InteractionSpec

class ToggleTool(
    val spec: Map<String, Any>
) {
    val name = spec.getValue("name") as String
    val label = spec.getValue("label") as String

    @Suppress("UNCHECKED_CAST")
    val interactionSpecList: List<InteractionSpec> = spec.getValue("interactions") as List<InteractionSpec>

    var active: Boolean = false
}
