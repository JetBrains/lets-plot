/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.FeatureSwitch.GGGRID_COLLECT_LEGENDS
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendsBlockInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.FigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayoutInfo
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
            title = title,
            subtitle = subtitle,
            caption = caption,
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

        // Lay out inner positions relative to the left-top of the figure.
        val contextBounds = DoubleRectangle(DoubleVector.ZERO, bounds.dimension)
        val withoutMargins = plotTheme.layoutMargins().shrinkRect(contextBounds)
        val withoutTitles = PlotLayoutUtil.boundsWithoutTitleAndCaption(
            outerBounds = withoutMargins,
            title, subtitle, caption, theme
        )

        // NEW: Subtract legend space if collecting legends
        val withoutPlotInset = plotTheme.plotInset().shrinkRect(withoutTitles)
        val elementsAreaBounds = if (GGGRID_COLLECT_LEGENDS) {
            // TODO: Perform a preliminary layout to collect legend sizes
            // For now, just use the bounds without legend space
            withoutPlotInset
        } else {
            withoutPlotInset
        }

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

    /**
     * Collect legends from individual plot elements that have fixed positions (LEFT, RIGHT, TOP, BOTTOM).
     * Returns a map of position to merged LegendsBlockInfo.
     */
    private fun collectFixedPositionLegends(elements: List<FigureBuildInfo?>): Map<LegendPosition, LegendsBlockInfo> {
        val collectedByPosition = mutableMapOf<LegendPosition, MutableList<LegendsBlockInfo>>()

        for (element in elements.filterNotNull()) {
            // Only collect from individual plots, not nested composites
            if (element.isComposite) continue

            val plotElement = element as? PlotFigureBuildInfo ?: continue

            // Access layout info to get legends
            val layoutInfo = plotElement.layoutInfo as? PlotFigureLayoutInfo ?: continue
            val legendsBlockInfo = layoutInfo.legendsBlockInfo

            if (legendsBlockInfo.boxWithLocationList.isEmpty()) continue

            // Determine legend position from theme
            // TODO: Need access to theme from PlotFigureBuildInfo
            // For now, skip - this will be implemented in next step
        }

        return emptyMap()
    }

    /**
     * Calculate space needed for collected legends and subtract from bounds.
     * Similar to legendBlockDelta in PlotLayoutUtil.kt:247
     */
    private fun subtractLegendSpace(
        bounds: DoubleRectangle,
        legendsInfo: LegendsBlockInfo,
        position: LegendPosition
    ): DoubleRectangle {
        if (legendsInfo.boxWithLocationList.isEmpty()) return bounds

        val size = legendsInfo.size()
        val spacing = theme.legend().boxSpacing()

        return when (position) {
            LegendPosition.LEFT -> {
                // Legends on left: shift right and reduce width
                DoubleRectangle(
                    origin = bounds.origin.add(DoubleVector(size.x + spacing, 0.0)),
                    dimension = bounds.dimension.subtract(DoubleVector(size.x + spacing, 0.0))
                )
            }
            LegendPosition.RIGHT -> {
                // Legends on right: reduce width
                DoubleRectangle(
                    origin = bounds.origin,
                    dimension = bounds.dimension.subtract(DoubleVector(size.x + spacing, 0.0))
                )
            }
            LegendPosition.TOP -> {
                // Legends on top: shift down and reduce height
                DoubleRectangle(
                    origin = bounds.origin.add(DoubleVector(0.0, size.y + spacing)),
                    dimension = bounds.dimension.subtract(DoubleVector(0.0, size.y + spacing))
                )
            }
            LegendPosition.BOTTOM -> {
                // Legends on bottom: reduce height
                DoubleRectangle(
                    origin = bounds.origin,
                    dimension = bounds.dimension.subtract(DoubleVector(0.0, size.y + spacing))
                )
            }
            else -> bounds // Overlay or hidden positions don't affect bounds
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