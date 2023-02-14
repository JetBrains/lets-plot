/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureSvgRoot
import jetbrains.datalore.plot.builder.GeomLayer

interface FigureBuildInfo {
    val bounds: DoubleRectangle
    val computationMessages: List<String>
    val containsLiveMap: Boolean

    fun createSvgRoot(): FigureSvgRoot

    fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any)

    fun withBounds(bounds: DoubleRectangle): FigureBuildInfo

    fun layoutedByOuterSize(size: DoubleVector): FigureBuildInfo
}