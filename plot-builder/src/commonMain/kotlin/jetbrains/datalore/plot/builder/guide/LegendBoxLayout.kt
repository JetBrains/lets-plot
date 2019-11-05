/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec

abstract class LegendBoxLayout protected constructor(
    private val myTitle: String,
    legendDirection: LegendDirection
) {

    protected abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL

    val titleHorizontalAnchor = TextLabel.HorizontalAnchor.LEFT
    val titleVerticalAnchor = if (isHorizontal) {
        TextLabel.VerticalAnchor.CENTER
    } else {
        TextLabel.VerticalAnchor.TOP
    }

    val titleBounds: DoubleRectangle
        get() {
            var origin = titleLocation
            val size = titleSize(myTitle)
            if (isHorizontal) {
                origin = DoubleVector(origin.x, origin.y - size.y / 2)
            }
            return DoubleRectangle(origin, size)
        }

    val graphOrigin: DoubleVector
        get() = if (isHorizontal) {
            DoubleVector(titleSize(myTitle).x, 0.0)
        } else DoubleVector(0.0, titleSize(myTitle).y)

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                .union(titleBounds)
                .union(graphBounds)
            return titleAndContent.dimension
        }

    val titleLocation: DoubleVector
        get() = if (isHorizontal) {
            val graphSize = graphSize
            DoubleVector(0.0, graphSize.y / 2)
        } else {
            // make some distance from the contents
            val y = -TITLE_SPEC.height() / 3
            DoubleVector(0.0, y)
        }

    companion object {
        private val TITLE_SPEC = PlotLabelSpec.LEGEND_TITLE
        internal val LABEL_SPEC = PlotLabelSpec.LEGEND_ITEM

        private fun titleSize(s: String): DoubleVector {
            return if (s.isBlank()) {
                DoubleVector.ZERO
            } else TITLE_SPEC.dimensions(s.length)
        }
    }
}
