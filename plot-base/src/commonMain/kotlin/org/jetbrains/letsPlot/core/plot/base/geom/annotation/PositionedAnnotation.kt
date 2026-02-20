/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class PositionedAnnotation(
    lines: List<LineSpec>,
    textStyle: TextStyle,
    useCustomColor: Boolean,
    useLayerColor: Boolean,
    val horizontalPlacements: List<HorizontalPlacement>,
    val verticalPlacements: List<VerticalPlacement>
): Annotation(lines, textStyle, useCustomColor, useLayerColor) {

    data class HorizontalPlacement(
        val position: Double?,
        val anchor: HorizontalAnchor
    )

    data class VerticalPlacement(
        val position: Double?,
        val anchor: VerticalAnchor
    )

    enum class HorizontalAnchor {
        LEFT, CENTER, RIGHT
    }

    enum class VerticalAnchor {
        TOP, CENTER, BOTTOM
    }

        companion object {
            val DEFAULT_HORIZONTAL_PLACEMENT = HorizontalPlacement(null, HorizontalAnchor.LEFT)
            val DEFAULT_VERTICAL_PLACEMENT = VerticalPlacement(null, VerticalAnchor.TOP)
        }
}