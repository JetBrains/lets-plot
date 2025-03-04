/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.FigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot

internal class CompositeFigureBuildInfo constructor(
    private val elements: List<FigureBuildInfo?>,
    private val layout: CompositeFigureLayout,
    override val bounds: DoubleRectangle,
    private val title: String?,
    private val subtitle: String?,
    private val caption: String?,
    private val theme: Theme,
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

        val overalSize = layoutInfo.figureSize
        val elementsAreaBounds = layoutInfo.geomAreaBounds
        val svgComponent = CompositeFigureSvgComponent(
            elementSvgRoots,
            overalSize,
            elementsAreaBounds,
            title, subtitle, caption,
            theme = theme,
            styleSheet = Style.fromTheme(theme, flippedAxis = false),
        )
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
                title, subtitle, caption,
                theme,
                computationMessages
            )
        }
    }

    override fun layoutedByOuterSize(): CompositeFigureBuildInfo {
        val plotTheme = theme.plot()

        // Layout inner positions relative to left-top of the figure.
        val contextBounds = DoubleRectangle(DoubleVector.ZERO, bounds.dimension)
        val withoutMargins = plotTheme.layoutMargins().shrinkRect(contextBounds)
        val withoutTitles = PlotLayoutUtil.boundsWithoutTitleAndCaption(
            outerBounds = withoutMargins,
            title, subtitle, caption, theme
        )
        val elementsAreaBounds = plotTheme.plotInset().shrinkRect(withoutTitles)

        val layoutedElements = layout.doLayout(elementsAreaBounds, elements)
        val layoutedElementsAreaBounds = layoutedElements.filterNotNull()
            .map { it.bounds }
            .reduceOrNull { acc, el -> acc.union(el) }
            ?: contextBounds

        return CompositeFigureBuildInfo(
            elements = layoutedElements,
            layout,
            bounds,
            title, subtitle, caption,
            theme,
            computationMessages
        ).apply {
            this._layoutInfo = FigureLayoutInfo(
                figureSize = contextBounds.dimension,
                geomAreaBounds = layoutedElementsAreaBounds
            )
        }
    }

    override fun layoutedByGeomBounds(geomBounds: DoubleRectangle): CompositeFigureBuildInfo {
        UNSUPPORTED("Composite figure does not support layouting by \"geometry bounds\".")
    }

    override fun withPreferredSize(size: DoubleVector): FigureBuildInfo {
        return CompositeFigureBuildInfo(
            elements,
            layout,
            DoubleRectangle(DoubleVector.ZERO, size),
            title, subtitle, caption,
            theme,
            computationMessages
        )
    }
}