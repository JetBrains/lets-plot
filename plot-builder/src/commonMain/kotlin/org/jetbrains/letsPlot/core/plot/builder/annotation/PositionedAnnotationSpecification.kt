/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.HorizontalPlacement
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.VerticalPlacement
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

class PositionedAnnotationSpecification(
    valueSources: List<ValueSource>,
    linePatterns: List<LinePattern>,
    // other settings
    textSize: Double?,
    useLayerColor: Boolean,
    val horizontalPlacements: List<HorizontalPlacement>,
    val verticalPlacements: List<VerticalPlacement>
): AnnotationSpecification(valueSources, linePatterns, textSize, useLayerColor) {

     companion object {
         val NONE = PositionedAnnotationSpecification(
                 valueSources = emptyList(),
                 linePatterns = emptyList(),
                 textSize = null,
                 useLayerColor = false,
                 horizontalPlacements = emptyList(),
                 verticalPlacements = emptyList()
             )
     }
}