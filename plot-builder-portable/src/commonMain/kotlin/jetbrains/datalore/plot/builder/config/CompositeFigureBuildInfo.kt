/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.layout.CompositeFigureLayout
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgComponent
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot

class CompositeFigureBuildInfo(
    private val elements: List<FigureBuildInfo?>,
    private val layout: CompositeFigureLayout,
    override val bounds: DoubleRectangle,
) : FigureBuildInfo {

    override val computationMessages: List<String>
        get() = elements.filterNotNull().flatMap { it.computationMessages }

    override val containsLiveMap: Boolean
        get() = elements.filterNotNull().any { it.containsLiveMap }

    override fun withBounds(bounds: DoubleRectangle): FigureBuildInfo {
        return CompositeFigureBuildInfo(
            elements,
            layout,
            bounds
        )
    }

    override fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any) {
        elements.filterNotNull().forEach {
            it.injectLiveMapProvider(f)
        }
    }

    override fun createSvgRoot(): CompositeFigureSvgRoot {
        val layouted = layout.doLayout(bounds.dimension, elements)
        val figures = layouted.filterNotNull().map {
            it.createSvgRoot()
        }

        return CompositeFigureSvgRoot(
            svgComponent = CompositeFigureSvgComponent(figures),
            bounds
        )
    }
}