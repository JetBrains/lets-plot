/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.textDimensions

abstract class LegendBoxLayout(
    private val title: String,
    legendDirection: LegendDirection,
    protected val theme: LegendTheme
) {
    // legend keys/colorbar + labels.
    abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL

    private val titleBounds: DoubleRectangle
        get() {
            return DoubleRectangle(DoubleVector.ZERO, titleSize)
        }

    val graphOrigin: DoubleVector
        get() = when {
            isHorizontal -> with(titleSize) {
                DoubleVector(
                    x,
                    if (y > graphSize.y) (y - graphSize.y) / 2 else 0.0
                )
            }

            else -> DoubleVector(
                0.0,
                titleSize.y.let { titleHeight ->
                    if (titleHeight > 0) {
                        // make some space between title and the rest of the content
                        titleHeight + PlotLabelSpecFactory.legendTitle(theme).height() / 2
                    } else {
                        0.0
                    }
                }
            )
        }

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                .union(titleBounds)
                .union(graphBounds)
            return titleAndContent.dimension
        }

    internal val titleSize: DoubleVector
        get() {
            return when {
                title.isBlank() || !theme.showTitle() -> DoubleVector.ZERO
                else -> textDimensions(title, PlotLabelSpecFactory.legendTitle(theme))
            }
        }
}
