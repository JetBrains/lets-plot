/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.toolbox

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class ToolboxControl(
    private val buttons: List<ToggleButtonControl>
) : SvgControl() {

    override val size: DoubleVector
        get() = DoubleVector(
            x = buttons.sumOf { it.size.x },
            y = buttons.maxOf { it.size.y }
        )

    init {
        // "layout"
        var x = 0.0
        buttons.forEach {
            it.origin = DoubleVector(x, 0.0)
            x += it.size.x
        }
        buttons.forEach(this::addChildren)
    }

    override fun onMouseLeft(e: MouseEvent) {
        svgRoot.opacity().set(0.2)
    }

    override fun onMouseEntered(e: MouseEvent) {
        svgRoot.opacity().set(1.0)
    }
}
