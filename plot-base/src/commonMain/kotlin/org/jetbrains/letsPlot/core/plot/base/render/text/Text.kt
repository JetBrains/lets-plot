/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

internal class Text(
    val text: String,
) : Term {
    override val visualCharCount: Int = text.length
    override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(text))

    override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
        return widthCalculator(text, font)
    }
}
