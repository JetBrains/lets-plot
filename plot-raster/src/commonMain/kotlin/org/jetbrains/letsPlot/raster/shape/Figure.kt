/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.values.Color

internal abstract class Figure : Element() {
    var stroke: Color? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Double> by visualProp(emptyList())
    var strokeDashOffset: Float by visualProp(0f)
    var strokeMiter: Float? by visualProp(null) // not mandatory, default works fine

    var fill: Color? by visualProp(null)
    var fillOpacity: Float by visualProp(1f)

    val fillPaint: Paint? by computedProp(Figure::fill, Figure::fillOpacity, managed = true) {
        return@computedProp fillPaint(fill, fillOpacity)
    }

    val strokePaint: Paint? by computedProp(
        Figure::stroke,
        Figure::strokeWidth,
        Figure::strokeDashArray,
        Figure::strokeOpacity,
        Figure::strokeMiter,
        managed = true
    ) {
        return@computedProp strokePaint(
            stroke = stroke,
            strokeWidth = strokeWidth,
            strokeOpacity = strokeOpacity,
            strokeDashArray = strokeDashArray,
            strokeDashOffset = strokeDashOffset,
            strokeMiter = strokeMiter
        )
    }
}