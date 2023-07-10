/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.FigureLayoutInfo
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgComponent
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot

internal class CompositeFigureBuildInfo(
    private val elements: List<FigureBuildInfo?>,
    private val layout: CompositeFigureLayout,
    override val bounds: DoubleRectangle,
    override val computationMessages: List<String>,
) : FigureBuildInfo {

    override val isComposite: Boolean = true

    override val layoutInfo: FigureLayoutInfo
        get() = _layoutInfo

    override val containsLiveMap: Boolean
        get() = elements.filterNotNull().any { it.containsLiveMap }

    private lateinit var _layoutInfo: FigureLayoutInfo


    override fun injectLiveMapProvider(f: (tiles: List<List<GeomLayer>>, spec: Map<String, Any>) -> Any) {
        elements.filterNotNull().forEach {
            it.injectLiveMapProvider(f)
        }
    }

    override fun createSvgRoot(): CompositeFigureSvgRoot {
        check(this::_layoutInfo.isInitialized) { "Composite figure is not layouted." }
        val elementSvgRoots = elements.filterNotNull().map {
            it.createSvgRoot()
        }

        val svgComponent = CompositeFigureSvgComponent(elementSvgRoots)
        return CompositeFigureSvgRoot(svgComponent, bounds)
    }

    override fun withBounds(bounds: DoubleRectangle): CompositeFigureBuildInfo {
        return if (bounds == this.bounds) {
            this
        } else {
            // this drops 'layout info' if initialized.
            CompositeFigureBuildInfo(
                elements,
                layout,
                bounds,
                computationMessages
            )
        }
    }

    override fun layoutedByOuterSize(): CompositeFigureBuildInfo {
        val outerSize = bounds.dimension
        val layoutedElements = layout.doLayout(outerSize, elements)

        val geomBounds = layoutedElements.filterNotNull().map {
            it.layoutInfo.geomAreaBounds
        }.reduce { acc, el -> acc.union(el) }

        return CompositeFigureBuildInfo(
            elements = layoutedElements,
            layout,
            bounds,
            computationMessages
        ).apply {
            this._layoutInfo = FigureLayoutInfo(outerSize, geomBounds)
        }
    }

    override fun layoutedByGeomBounds(geomBounds: DoubleRectangle): CompositeFigureBuildInfo {
        UNSUPPORTED("Composite figure does not support layouting by \"geometry bounds\".")
    }
}