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

class PlotFigureLayoutInfo(
    // ToDo: rename to "figure..."
    val plotOuterBounds:DoubleRectangle,
    val plotOuterBoundsWithoutTitleAndCaption:DoubleRectangle,

    val plotInnerOrigin:DoubleVector, // Inner bounds - all without titles and legends.
    val geomAreaBounds: DoubleRectangle,

    outerSize: DoubleVector,
    val plotLayoutInfo: PlotLayoutInfo,

    val legendsBlockInfo: LegendsBlockInfo,

) : FigureLayoutInfo(outerSize, plotLayoutInfo.geomOuterBounds)