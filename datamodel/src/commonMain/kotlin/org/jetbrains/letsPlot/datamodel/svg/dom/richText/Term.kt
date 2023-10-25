/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.richText

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

interface Term {
    fun toSvg(): List<SvgTSpanElement>

    fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double

    fun getHeight(labelHeight: Double): Double
}