/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgComponent
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot

internal class CompositeFigureBuildInfo(
    private val elements: List<FigureBuildInfo?>,
    private val layout: CompositeFigureLayout,
    override val bounds: DoubleRectangle,
) : FigureBuildInfo {


    override val layoutInfo: FigureLayoutInfo
        get() = _layoutInfo

    override val computationMessages: List<String>
        get() = elements.filterNotNull().flatMap { it.computationMessages }

    override val containsLiveMap: Boolean
        get() = elements.filterNotNull().any { it.containsLiveMap }

    private lateinit var _layoutInfo: FigureLayoutInfo
    private lateinit var layoutedElements: List<FigureBuildInfo>


    override fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any) {
        elements.filterNotNull().forEach {
            it.injectLiveMapProvider(f)
        }
    }

    override fun createSvgRoot(): CompositeFigureSvgRoot {
        return if (this::_layoutInfo.isInitialized) {
            val elementSvgRoots = layoutedElements.map {
                it.createSvgRoot()
            }

            val svgComponent = CompositeFigureSvgComponent(elementSvgRoots)
            CompositeFigureSvgRoot(svgComponent, bounds)
        } else {
            layoutedByOuterSize().createSvgRoot()
        }
    }

    override fun withBounds(bounds: DoubleRectangle): CompositeFigureBuildInfo {
        return CompositeFigureBuildInfo(
            elements,
            layout,
            bounds
        )
    }

    override fun layoutedByOuterSize(): CompositeFigureBuildInfo {
        val outerSize = bounds.dimension
        val layoutedElements = layout.doLayout(outerSize, elements)

        val geomBounds = layoutedElements.map {
            it.layoutInfo.geomAreaBounds
        }.reduce { acc, el -> acc.union(el) }

        return CompositeFigureBuildInfo(
            elements,
            layout,
            bounds
        ).apply {
            this._layoutInfo = FigureLayoutInfo(outerSize, geomBounds)
            this.layoutedElements = layoutedElements
        }
    }

    override fun layoutedByGeomBounds(geomBounds: DoubleRectangle): CompositeFigureBuildInfo {
        UNSUPPORTED("Not yet implemented")
    }
}