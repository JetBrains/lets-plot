/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.core.plot.base.geom.annotation.SmoothSummaryAnnotation.LabelX
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.SmoothSummaryAnnotation.LabelY
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

class SmoothAnnotationSpecification(
    valueSources: List<ValueSource>,
    linePatterns: List<LinePattern>,
    // other settings
    textSize: Double?,
    useLayerColor: Boolean,
    val labelX: List<Pair<Double?, LabelX>>,
    val labelY: List<Pair<Double?, LabelY>>
): AnnotationSpecification(valueSources, linePatterns, textSize, useLayerColor) {

     companion object {
         val NONE = SmoothAnnotationSpecification(
                 valueSources = emptyList(),
                 linePatterns = emptyList(),
                 textSize = null,
                 useLayerColor = false,
                 labelX = emptyList(),
                 labelY = emptyList()
             )
     }
}