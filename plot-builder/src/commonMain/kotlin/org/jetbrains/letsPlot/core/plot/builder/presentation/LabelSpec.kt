/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.LineDimensions
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics
import kotlin.math.max

interface LabelSpec {
    val font: Font
    val markdown: Boolean

    fun lineDimensions(labelText: String): List<LineDimensions>

    fun defaultLine(): LineDimensions
}

fun LabelSpec.lineLayoutMetrics(labelText: String): List<LineLayoutMetrics> =
    lineDimensions(labelText).map(LineDimensions::layoutMetrics)

fun LabelSpec.totalDimensions(labelText: String): DoubleVector =
    lineDimensions(labelText).fold(DoubleVector.ZERO) { acc, dim ->
        DoubleVector(
            x = max(acc.x, dim.width),
            y = acc.y + dim.height
        )
    }

fun LabelSpec.width(labelText: String): Double =
    lineDimensions(labelText).maxOfOrNull(LineDimensions::width) ?: 0.0
