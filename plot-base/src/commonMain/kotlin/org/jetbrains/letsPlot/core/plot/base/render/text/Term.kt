/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement

internal interface Term {
    val visualCharCount: Int // in chars, used for line wrapping
    val svg: List<SvgElement>

    fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
}
