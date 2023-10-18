/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.formula

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

class Text(private val text: String) : Term {
    override fun toSvg(): List<SvgTSpanElement> {
        return listOf(SvgTSpanElement(text))
    }

    override fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
        return { font ->
            labelWidthCalculator(text, font)
        }
    }
}