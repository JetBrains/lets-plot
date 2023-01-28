/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotSvgContainer

interface FigureBuildInfo {
    val bounds: DoubleRectangle
    val computationMessages: List<String>
    val containsLiveMap: Boolean

    fun createFigure(): PlotSvgContainer

    fun forEachPlot(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Unit)
}