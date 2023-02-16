/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.PlotLayoutInfo
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo

class PlotFigureLayoutInfo(
    outerSize: DoubleVector,
    val plotLayoutInfo: PlotLayoutInfo
) : FigureLayoutInfo(outerSize, plotLayoutInfo.geomOuterBounds)