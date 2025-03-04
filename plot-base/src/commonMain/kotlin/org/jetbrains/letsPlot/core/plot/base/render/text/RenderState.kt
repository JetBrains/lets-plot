/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

data class RenderState(
    val isItalic: Boolean = false,
    val isBold: Boolean = false,
    val color: Color? = null
) {

    fun apply(tSpan: SvgTSpanElement): SvgTSpanElement {
        if (color != null) {
            tSpan.fillColor().set(color)
        }

        if (isBold) {
            tSpan.fontWeight().set("bold")
        }

        if (isItalic) {
            tSpan.fontStyle().set("italic")
        }

        return tSpan
    }
}
