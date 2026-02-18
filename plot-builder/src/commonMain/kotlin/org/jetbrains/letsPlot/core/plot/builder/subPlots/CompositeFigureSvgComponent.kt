/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.subPlots

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_DEBUG_DRAWING
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FigureSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxesLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class CompositeFigureSvgComponent constructor(
    internal val elements: List<FigureSvgRoot>,
    private val title: String?,
    private val subtitle: String?,
    private val caption: String?,
    private val tag: String?,
    private val layoutInfo: CompositeFigureLayoutInfo,
    val theme: Theme,
    val styleSheet: StyleSheet,
) : SvgComponent() {

    override fun buildComponent() {

        val outerBounds = DoubleRectangle(DoubleVector.ZERO, layoutInfo.figureSize)
        val elementsAreaBounds = layoutInfo.elementsAreaBounds

        val plotTheme = theme.plot()
        if (plotTheme.showBackground()) {
            val plotInset = Thickness.uniform(plotTheme.backgroundStrokeWidth() / 2)
            val backgroundRect = plotInset.shrinkRect(outerBounds)
            add(SvgRectElement(backgroundRect).apply {
                fillColor().set(plotTheme.backgroundFill())
                strokeColor().set(plotTheme.backgroundColor())
                strokeWidth().set(plotTheme.backgroundStrokeWidth())
                StrokeDashArraySupport.apply(this, plotTheme.backgroundStrokeWidth(), plotTheme.backgroundLineType())
            })
        }

        val contentAreaBounds = layoutInfo.contentAreaBounds

        if (DEBUG_DRAWING) {
            drawDebugRect(outerBounds, Color.BLUE, "BLUE: plotOuterBounds")
            drawDebugRect(outerBounds, Color.BLUE, "BLUE: contentAreaBounds")
            drawDebugRect(elementsAreaBounds, Color.RED, "RED: elementsAreaBounds")
        }

        val textLayout = PlotSvgComponentHelper.figureTextLayout(
            title = title,
            subtitle = subtitle,
            caption = caption,
            tag = tag,
            outerBounds = contentAreaBounds,
            geomOrElementsAreaBounds = elementsAreaBounds,
            plotTheme = plotTheme
        )

        PlotSvgComponentHelper.renderFigureTextElements(
            svg = this,
            title = title,
            subtitle = subtitle,
            caption = caption,
            tag = tag,
            textLayout = textLayout,
            plotTheme = plotTheme
        )

        if (DEBUG_DRAWING) {
            drawDebugRect(elementsAreaBounds, Color.RED, "RED: geomAreaBounds")

            PlotSvgComponentHelper.drawFigureTextFrames(
                this,
                title,
                subtitle,
                caption,
                tag,
                textLayout,
                plotTheme
            )
        }

        // Render collected legend blocks

        for (legendBlock in layoutInfo.legendsBlockInfos) {
            val position = legendBlock.position
            val justification = legendBlock.justification

            // Calculate legend origin
            val blockSize = legendBlock.size()
            val legendOrigin = if (position.isFixed) {
                LegendBoxesLayoutUtil.overlayLegendOriginOutsidePlot(
                    innerBounds = elementsAreaBounds,
                    outerBounds = textLayout.outerBoundsWithoutTitleCaption,
                    legendSize = blockSize,
                    legendPosition = position,
                    legendJustification = justification
                )
            } else {
                LegendBoxesLayoutUtil.overlayLegendOriginInsidePlot(
                    plotBounds = elementsAreaBounds,
                    legendSize = blockSize,
                    legendPosition = position,
                    legendJustification = justification
                )
            }

            // Position and render each legend box in the block
            val positionedLegends = legendBlock.moveAll(legendOrigin)
            for (boxWithLocation in positionedLegends.boxWithLocationList) {
                val legendBox = boxWithLocation.legendBox.createSvgComponent()
                legendBox.moveTo(boxWithLocation.location)
                add(legendBox)
            }
        }
    }

    override fun clear() {
        super.clear()
    }

    companion object {
        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }
}