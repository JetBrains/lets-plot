/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.LegendsBlockInfo
import jetbrains.datalore.plot.builder.layout.PlotLayoutInfo
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo

class PlotFigureLayoutInfo constructor(
    val figureLayoutedBounds: DoubleRectangle,
    val figureBoundsWithoutTitleAndCaption: DoubleRectangle,
    /**
     * Origin of the plot area: geoms, axis and facet labels (no titles, legends).
     * Relative to the figure origin (see `figureLayoutedBounds`)
     */
    val plotAreaOrigin: DoubleVector, // Inner bounds - all without titles and legends.
    /**
     * Plot withot axis and facet labels.
     * Relative to the figure origin (see `figureLayoutedBounds`)
     */
    geomAreaBounds: DoubleRectangle,
    figurePreferredSize: DoubleVector,
    val plotLayoutInfo: PlotLayoutInfo,
    val legendsBlockInfo: LegendsBlockInfo,

    ) : FigureLayoutInfo(figurePreferredSize, geomAreaBounds)