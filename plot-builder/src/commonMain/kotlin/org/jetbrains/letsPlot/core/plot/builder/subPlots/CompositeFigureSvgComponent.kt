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
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.addTitle
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.captionElementAndTextBounds
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.drawCaptionDebugInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.drawSubtitleDebugInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.drawTitleDebugInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.subtitleElementAndTextBounds
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponentHelper.titleElementAndTextBounds
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxesLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class CompositeFigureSvgComponent constructor(
    internal val elements: List<FigureSvgRoot>,
    title: String?,
    subtitle: String?,
    caption: String?,
    private val layoutInfo: CompositeFigureLayoutInfo,
    val theme: Theme,
    val styleSheet: StyleSheet,
) : SvgComponent() {

    private val title: String? = title?.takeIf { theme.plot().showTitle() }
    private val subtitle: String? = subtitle?.takeIf { theme.plot().showSubtitle() }
    private val caption: String? = caption?.takeIf { theme.plot().showCaption() }

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

        // plot title, subtitle, caption rectangles:
        //   xxxElementRect - rectangle for the element, including margins
        //   xxxTextRect - for text only

        if (DEBUG_DRAWING) {
            drawDebugRect(outerBounds, Color.BLUE, "BLUE: plotOuterBounds")
        }

//        // Exclude plot border and margin
//        val plotLayoutMargins = theme.plot().layoutMargins()
//        val contentAreaBounds = plotLayoutMargins.shrinkRect(outerBounds)
        val contentAreaBounds = layoutInfo.contentAreaBounds
        if (DEBUG_DRAWING) {
            drawDebugRect(outerBounds, Color.BLUE, "BLUE: contentAreaBounds")
        }

        if (DEBUG_DRAWING) {
            drawDebugRect(elementsAreaBounds, Color.RED, "RED: elementsAreaBounds")
        }

        val (plotTitleElementRect, plotTitleTextRect) = titleElementAndTextBounds(
            title,
            contentAreaBounds,
            elementsAreaBounds,
            plotTheme
        )
        if (DEBUG_DRAWING) {
            drawTitleDebugInfo(this, caption, plotTitleElementRect, plotTitleTextRect, plotTheme)
        }

        val (subtitleElementRect, subtitleTextRect) = subtitleElementAndTextBounds(
            subtitle,
            contentAreaBounds,
            elementsAreaBounds,
            plotTitleElementRect,
            plotTheme
        )
        if (DEBUG_DRAWING) {
            drawSubtitleDebugInfo(this, subtitle, subtitleElementRect, subtitleTextRect, plotTheme)
        }

        val (captionElementRect, captionTextRect) = captionElementAndTextBounds(
            caption,
            contentAreaBounds,
            elementsAreaBounds,
            plotTheme
        )
        if (DEBUG_DRAWING) {
            drawCaptionDebugInfo(this, caption, captionElementRect, captionTextRect, plotTheme)
        }

        // add plot title
        plotTitleTextRect?.let {
            addTitle(
                svgComponent = this,
                title,
                labelSpec = PlotLabelSpecFactory.plotTitle(plotTheme),
                justification = plotTheme.titleJustification(),
                boundRect = it,
                className = Style.PLOT_TITLE
            )
        }
        // add plot subtitle
        subtitleTextRect?.let {
            addTitle(
                svgComponent = this,
                subtitle,
                labelSpec = PlotLabelSpecFactory.plotSubtitle(plotTheme),
                justification = plotTheme.subtitleJustification(),
                boundRect = it,
                className = Style.PLOT_SUBTITLE
            )
        }
        // add caption
        captionTextRect?.let {
            addTitle(
                svgComponent = this,
                text = caption,
                labelSpec = PlotLabelSpecFactory.plotCaption(plotTheme),
                justification = plotTheme.captionJustification(),
                boundRect = it,
                className = Style.PLOT_CAPTION
            )
        }

        // Render collected legend blocks
        // Use bounds without title and caption, similar to PlotSvgComponent
        val outerBoundsWithoutTitleAndCaption =
            PlotLayoutUtil.boundsWithoutTitleAndCaption(
                outerBounds = contentAreaBounds,
                title = title,
                subtitle = subtitle,
                caption = caption,
                theme = theme
            )

        for (legendBlock in layoutInfo.legendsBlockInfos) {
            val position = legendBlock.position
            val justification = legendBlock.justification
            val legendsBlockInfo = legendBlock.legendsBlockInfo

            // Calculate legend origin
            val blockSize = legendsBlockInfo.size()
            val legendOrigin = if (position.isFixed) {
                LegendBoxesLayoutUtil.overlayLegendOriginOutsidePlot(
                    innerBounds = elementsAreaBounds,
                    outerBounds = outerBoundsWithoutTitleAndCaption,
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
            val positionedLegends = legendsBlockInfo.moveAll(legendOrigin)
            for (boxWithLocation in positionedLegends.boxWithLocationList) {
                val legendBox = boxWithLocation.legendBox.createLegendBox()
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