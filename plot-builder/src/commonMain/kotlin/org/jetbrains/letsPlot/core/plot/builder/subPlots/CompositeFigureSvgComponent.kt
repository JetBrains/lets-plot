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
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class CompositeFigureSvgComponent(
    internal val elements: List<FigureSvgRoot>,
    private val size: DoubleVector,
    private val elementsAreaBounds: DoubleRectangle,
    private val title: String?,
    private val subtitle: String?,
    private val caption: String?,
    val theme: Theme,
    val styleSheet: StyleSheet,
) : SvgComponent() {

    override fun buildComponent() {

        val plotTheme = theme.plot()
        if (plotTheme.showBackground()) {
            val r = DoubleRectangle(DoubleVector.ZERO, size)
            val plotInset = Thickness.uniform(plotTheme.backgroundStrokeWidth() / 2)
            val backgroundRect = plotInset.shrinkRect(r)
            add(SvgRectElement(backgroundRect).apply {
                fillColor().set(plotTheme.backgroundFill())
                strokeColor().set(plotTheme.backgroundColor())
                strokeWidth().set(plotTheme.backgroundStrokeWidth())
                StrokeDashArraySupport.apply(this, plotTheme.backgroundStrokeWidth(), plotTheme.backgroundLineType())
            })
        }

        // plot title, subtitle, caption rectangles:
        //   xxxElementRect - rectangle for element, including margins
        //   xxxTextRect - for text only

        val outerBounds = DoubleRectangle(DoubleVector.ZERO, size)
        if (DEBUG_DRAWING) {
            drawDebugRect(outerBounds, Color.BLUE, "BLUE: plotOuterBounds")
        }

        // Exclude plot border and margin
        val plotLayoutMargins = theme.plot().layoutMargins()
        val contentAreaBounds = plotLayoutMargins.shrinkRect(outerBounds)
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

    }

    override fun clear() {
        super.clear()
    }

    companion object {
        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }
}