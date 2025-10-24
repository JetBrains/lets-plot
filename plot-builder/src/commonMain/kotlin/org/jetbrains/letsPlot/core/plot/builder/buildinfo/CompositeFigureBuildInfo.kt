/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.buildinfo

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendsBlockInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtilNew
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot

class CompositeFigureBuildInfo constructor(
    private val elements: List<FigureBuildInfo?>,
    private val layout: CompositeFigureLayout,
    override val bounds: DoubleRectangle,
    private val title: String?,
    private val subtitle: String?,
    private val caption: String?,
    private val theme: Theme,
    override val computationMessages: List<String>,
    private val legendBlocks: List<LegendsBlockInfo>,
) : FigureBuildInfo {

    override val isComposite: Boolean = true

    override val layoutInfo: CompositeFigureLayoutInfo
        get() = _layoutInfo

    override val containsLiveMap: Boolean
        get() = elements.filterNotNull().any { it.containsLiveMap }

    private lateinit var _layoutInfo: CompositeFigureLayoutInfo


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

        val svgComponent = CompositeFigureSvgComponent(
            elementSvgRoots,
            title = title,
            subtitle = subtitle,
            caption = caption,
            layoutInfo = layoutInfo,
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
                computationMessages,
                legendBlocks,
            )
        }
    }

    override fun layoutedByOuterSize(): CompositeFigureBuildInfo {
        val plotTheme = theme.plot()

        // Lay out inner positions relative to the left-top of the figure.
        val outerBounds = DoubleRectangle(DoubleVector.ZERO, bounds.dimension)

        // Exclude plot border and margin
        val plotLayoutMargins = theme.plot().layoutMargins()
        val contentAreaBounds = plotLayoutMargins.shrinkRect(outerBounds)

        val withoutTitles = PlotLayoutUtil.boundsWithoutTitleAndCaption(
            outerBounds = contentAreaBounds,
            title, subtitle, caption, theme
        )

        val withoutPlotInset = plotTheme.plotInset().shrinkRect(withoutTitles)

        // Subtract space for fixed-position legend blocks
        val elementsAreaBounds = PlotLayoutUtilNew.subtractLegendsSpace(
            bounds = withoutPlotInset,
            legendBlocks = legendBlocks,
            theme = theme.legend()
        )

        val layoutedElements = layout.doLayout(elementsAreaBounds, elements)
        val layoutedElementsAreaBounds = layoutedElements.filterNotNull()
            .map { it.bounds }
            .reduceOrNull { acc, el -> acc.union(el) }
            ?: elementsAreaBounds

        return CompositeFigureBuildInfo(
            elements = layoutedElements,
            layout,
            bounds,
            title, subtitle, caption,
            theme,
            computationMessages,
            legendBlocks
        ).apply {
            this._layoutInfo = CompositeFigureLayoutInfo(
                figureSize = outerBounds.dimension,
                contentAreaBounds = contentAreaBounds,
                elementsAreaBounds = layoutedElementsAreaBounds,
                legendsBlockInfos = legendBlocks
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
            DoubleRectangle(DoubleVector.Companion.ZERO, size),
            title, subtitle, caption,
            theme,
            computationMessages,
            legendBlocks
        )
    }
}