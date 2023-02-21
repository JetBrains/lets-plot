/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo

interface FigureBuildInfo {
    val isComposite:Boolean
    val bounds: DoubleRectangle
    val computationMessages: List<String>
    val containsLiveMap: Boolean
    val layoutInfo: FigureLayoutInfo

    fun createSvgRoot(): FigureSvgRoot

    fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any)

    fun withBounds(bounds: DoubleRectangle): FigureBuildInfo

    fun layoutedByOuterSize(): FigureBuildInfo

    fun layoutedByGeomBounds(geomBounds: DoubleRectangle): FigureBuildInfo
}