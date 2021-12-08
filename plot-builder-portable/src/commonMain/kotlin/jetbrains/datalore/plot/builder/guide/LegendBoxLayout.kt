/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec

abstract class LegendBoxLayout(
    private val title: String,
    legendDirection: LegendDirection
) {
    // legend keys/colorbar + labels.
    abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL
    val titleHorizontalAnchor = TextLabel.HorizontalAnchor.LEFT
    val titleVerticalAnchor = if (isHorizontal) {
        TextLabel.VerticalAnchor.CENTER
    } else {
        TextLabel.VerticalAnchor.TOP
    }

    val titleLocation: DoubleVector
        get() = if (isHorizontal) {
            DoubleVector(0.0, graphSize.y / 2)
        } else {
            DoubleVector.ZERO
        }

    val titleBounds: DoubleRectangle
        get() {
            var origin = titleLocation
            val size = titleSize(title)
            if (isHorizontal) {
                origin = DoubleVector(origin.x, origin.y - size.y / 2)
            }
            return DoubleRectangle(origin, size)
        }

    val graphOrigin: DoubleVector
        get() = when {
            isHorizontal ->
                DoubleVector(titleSize(title).x, 0.0)
            else -> {
                // make some space betwee title and the rest of the content.
                val y = TITLE_SPEC.height() + TITLE_SPEC.height() / 3
                DoubleVector(0.0, y)
            }
        }

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                .union(titleBounds)
                .union(graphBounds)
            return titleAndContent.dimension
        }

    companion object {
        private val TITLE_SPEC = PlotLabelSpec.LEGEND_TITLE
        internal val LABEL_SPEC = PlotLabelSpec.LEGEND_ITEM

        private fun titleSize(s: String): DoubleVector {
            return when {
                s.isBlank() -> DoubleVector.ZERO
                else -> TITLE_SPEC.dimensions(s.length)
            }
        }
    }
}
