/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.TextRotation
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.applyJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.base.theme.TitlePosition
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec

internal object PlotSvgComponentHelper {
    private fun textRectangle(elementRect: DoubleRectangle, margins: Thickness) = createTextRectangle(
        elementRect,
        topMargin = margins.top,
        rightMargin = margins.right,
        bottomMargin = margins.bottom,
        leftMargin = margins.left
    )

    internal fun createTextRectangle(
        elementRect: DoubleRectangle,
        topMargin: Double = 0.0,
        rightMargin: Double = 0.0,
        bottomMargin: Double = 0.0,
        leftMargin: Double = 0.0
    ) = DoubleRectangle(
        elementRect.left + leftMargin,
        elementRect.top + topMargin,
        elementRect.width - (rightMargin + leftMargin),
        elementRect.height - (topMargin + bottomMargin)
    )

    /**
     *   xxxElementRect - rectangle for element, including margins
     *   xxxTextRect - for text only
     */
    fun titleElementAndTextBounds(
        title: String?,
        plotOuterBounds: DoubleRectangle,
        geomAreaBounds: DoubleRectangle,
        plotTheme: PlotTheme
    ): Pair<DoubleRectangle?, DoubleRectangle?> {
        if (title == null) return Pair(null, null)

        val titleAlignmentArea = when (plotTheme.titlePosition()) {
            TitlePosition.PANEL -> geomAreaBounds
            TitlePosition.PLOT -> plotOuterBounds
        }
        val elementRect = DoubleRectangle(
            titleAlignmentArea.left,
            plotOuterBounds.top,
            titleAlignmentArea.width,
            PlotLayoutUtil.titleThickness(
                title,
                PlotLabelSpecFactory.plotTitle(plotTheme),
                plotTheme.titleMargins()
            )
        )
        val textRect = textRectangle(elementRect, plotTheme.titleMargins())
        return Pair(elementRect, textRect)
    }

    /**
     *   xxxElementRect - rectangle for element, including margins
     *   xxxTextRect - for text only
     */
    fun subtitleElementAndTextBounds(
        subtitle: String?,
        plotOuterBounds: DoubleRectangle,
        geomAreaBounds: DoubleRectangle,
        titleElementRect: DoubleRectangle?,
        plotTheme: PlotTheme
    ): Pair<DoubleRectangle?, DoubleRectangle?> {
        if (subtitle == null) return Pair(null, null)

        val titleAlignmentArea = when (plotTheme.titlePosition()) {
            TitlePosition.PANEL -> geomAreaBounds
            TitlePosition.PLOT -> plotOuterBounds
        }

        val elementRect = DoubleRectangle(
            titleAlignmentArea.left,
            titleElementRect?.bottom ?: plotOuterBounds.top,
            titleAlignmentArea.width,
            PlotLayoutUtil.titleThickness(
                subtitle,
                PlotLabelSpecFactory.plotSubtitle(plotTheme),
                plotTheme.subtitleMargins()
            )
        )

        val textRect = textRectangle(elementRect, plotTheme.subtitleMargins())
        return Pair(elementRect, textRect)
    }


    /**
     *   xxxElementRect - rectangle for element, including margins
     *   xxxTextRect - for text only
     */
    fun captionElementAndTextBounds(
        caption: String?,
        plotOuterBounds: DoubleRectangle,
        geomAreaBounds: DoubleRectangle,
        plotTheme: PlotTheme
    ): Pair<DoubleRectangle?, DoubleRectangle?> {
        if (caption == null) return Pair(null, null)

        val captionAlignmentArea = when (plotTheme.captionPosition()) {
            TitlePosition.PANEL -> geomAreaBounds
            TitlePosition.PLOT -> plotOuterBounds
        }
        val elementRect = caption.let {
            val captionRectHeight = PlotLayoutUtil.titleThickness(
                caption,
                PlotLabelSpecFactory.plotCaption(plotTheme),
                plotTheme.captionMargins()
            )
            DoubleRectangle(
                captionAlignmentArea.left,
                plotOuterBounds.bottom - captionRectHeight,
                captionAlignmentArea.width,
                captionRectHeight
            )
        }
        val textRect = textRectangle(elementRect, plotTheme.captionMargins())
        return Pair(elementRect, textRect)
    }

    fun addTitle(
        svgComponent: SvgComponent,
        text: String?,
        labelSpec: LabelSpec,
        justification: TextJustification,
        boundRect: DoubleRectangle,
        rotation: TextRotation? = null,
        className: String
    ) {
        if (text == null) return

        val lineHeight = labelSpec.height()
        val textLabel = MultilineLabel(text, markdown = labelSpec.markdown)
        textLabel.addClassName(className)
        val (position, hAnchor) = applyJustification(
            boundRect,
            textSize = PlotLayoutUtil.textDimensions(text, labelSpec),
            lineHeight,
            justification,
            rotation
        )
        textLabel.setLineHeight(lineHeight)
        textLabel.setHorizontalAnchor(hAnchor)
        textLabel.moveTo(position)
        rotation?.angle?.let(textLabel::rotate)
        svgComponent.add(textLabel)
    }


    fun drawTitleDebugInfo(
        svgComponent: SvgComponent,
        title: String?,
        elementRect: DoubleRectangle?,
        textRect: DoubleRectangle?,
        plotTheme: PlotTheme
    ) {
        textRect?.let { svgComponent.drawDebugRect(it, Color.LIGHT_BLUE) }
        elementRect?.let { svgComponent.drawDebugRect(it, Color.GRAY) }
        if (title != null && textRect != null) {
            svgComponent.drawDebugRect(
                textBoundingBox(
                    title,
                    textRect,
                    PlotLabelSpecFactory.plotTitle(plotTheme),
                    plotTheme.titleJustification()
                ),
                Color.DARK_GREEN
            )
        }
    }

    fun drawSubtitleDebugInfo(
        svgComponent: SvgComponent,
        subtitle: String?,
        elementRect: DoubleRectangle?,
        textRect: DoubleRectangle?,
        plotTheme: PlotTheme
    ) {
        textRect?.let { svgComponent.drawDebugRect(it, Color.LIGHT_BLUE) }
        elementRect?.let { svgComponent.drawDebugRect(it, Color.GRAY) }
        if (subtitle != null && textRect != null) {
            svgComponent.drawDebugRect(
                textBoundingBox(
                    subtitle,
                    textRect,
                    PlotLabelSpecFactory.plotSubtitle(plotTheme),
                    plotTheme.subtitleJustification()
                ),
                Color.DARK_GREEN
            )
        }
    }

    fun drawCaptionDebugInfo(
        svgComponent: SvgComponent,
        caption: String?,
        elementRect: DoubleRectangle?,
        textRect: DoubleRectangle?,
        plotTheme: PlotTheme
    ) {
        textRect?.let { svgComponent.drawDebugRect(it, Color.LIGHT_BLUE) }
        elementRect?.let { svgComponent.drawDebugRect(it, Color.GRAY) }
        if (caption != null && textRect != null) {
            svgComponent.drawDebugRect(
                textBoundingBox(
                    caption,
                    textRect,
                    PlotLabelSpecFactory.plotCaption(plotTheme),
                    plotTheme.captionJustification()
                ),
                Color.DARK_GREEN
            )
        }
    }

    // for debug drawing
    fun textBoundingBox(
        text: String,
        boundRect: DoubleRectangle,
        labelSpec: LabelSpec,
        justification: TextJustification,
        orientation: Orientation = Orientation.TOP,
    ): DoubleRectangle {
        val textDimensions = PlotLayoutUtil.textDimensions(text, labelSpec)
        return if (orientation.isHorizontal) {
            val x = (boundRect.left + boundRect.width * justification.x) - when {
                justification.x < 0.5 -> 0.0                        // left horizontal anchor is used
                justification.x == 0.5 -> textDimensions.x / 2      // middle
                else -> textDimensions.x                            // right
            }
            DoubleRectangle(x, boundRect.center.y - textDimensions.y / 2, textDimensions.x, textDimensions.y)
        } else {
            val y = (boundRect.bottom - boundRect.height * justification.x) - when {
                justification.x < 0.5 -> textDimensions.x
                justification.x == 0.5 -> textDimensions.x / 2
                else -> 0.0
            }
            DoubleRectangle(boundRect.center.x - textDimensions.y / 2, y, textDimensions.y, textDimensions.x)
        }
    }
}