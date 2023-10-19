/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.annotations

import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class Annotations(
    private val lines: List<LineSpec>,
    val textStyle: TextStyle
) {
    fun getAnnotationText(index: Int): String {
        return lines.mapNotNull { it.getAnnotationText(index) }.joinToString("\n")
    }
}