/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.layout.Margins
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.GEOM_AREA_PADDING
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.GEOM_MIN_SIZE
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.LIVE_MAP_PLOT_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.LIVE_MAP_PLOT_PADDING
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import kotlin.math.max

internal object PlotLayoutUtil {
    fun plotInsets(
        hAxisPosition: AxisPosition,
        vAxisPosition: AxisPosition,
        hAxisTheme: AxisTheme,
        vAxisTheme: AxisTheme
    ): Insets {
        val vPadding = if (hAxisTheme.showTitle() || hAxisTheme.showLabels()) 0.0 else GEOM_AREA_PADDING
        val hPadding = if (vAxisTheme.showTitle() || vAxisTheme.showLabels()) 0.0 else GEOM_AREA_PADDING
        val (left, right) = when (vAxisPosition) {
            AxisPosition.LEFT -> Pair(hPadding, GEOM_AREA_PADDING)
            AxisPosition.RIGHT -> Pair(GEOM_AREA_PADDING, hPadding)
            AxisPosition.LR -> Pair(hPadding, hPadding)
            else -> throw IllegalStateException("Illegal vertical axis position: $vAxisPosition")
        }
        val (top, bottom) = when (hAxisPosition) {
            AxisPosition.TOP -> Pair(vPadding, GEOM_AREA_PADDING)
            AxisPosition.BOTTOM -> Pair(GEOM_AREA_PADDING, vPadding)
            AxisPosition.TB -> Pair(vPadding, vPadding)
            else -> throw IllegalStateException("Illegal horizontal axis position: $hAxisPosition")
        }

        return Insets(left, top, right, bottom)
    }

    private fun labelDimensions(text: String, labelSpec: LabelSpec): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector(0.0, labelSpec.height(null))
        }
        return DoubleVector(
            labelSpec.width(text),
            labelSpec.height(text)
        )
    }

    private fun textLinesDimensions(text: String, labelSpec: LabelSpec): List<DoubleVector> {
        return MultilineLabel.splitLines(text).map { line -> labelDimensions(line, labelSpec) }
    }

    internal fun textDimensions(text: String, labelSpec: LabelSpec): DoubleVector {
        fun DoubleVector.union(p: DoubleVector): DoubleVector {
            return DoubleVector(
                x = max(x, p.x),
                y = y + p.y
            )
        }
        return textLinesDimensions(text, labelSpec)
            .fold(DoubleVector.ZERO) { acc, dv -> acc.union(dv) }
    }

    private fun titleThickness(title: String?, labelSpec: LabelSpec, margin: Double): Double {
        if (title == null) return 0.0
        return textDimensions(title, labelSpec).y + margin
    }

    internal fun titleThickness(title: String?, labelSpec: LabelSpec, margins: Margins): Double {
        return titleThickness(title, labelSpec, margin = margins.height())
    }

    fun overallGeomBounds(plotLayoutInfo: PlotLayoutInfo): DoubleRectangle {
        require(plotLayoutInfo.tiles.isNotEmpty()) { "Plot is empty" }
        return plotLayoutInfo.tiles.map { it.getAbsoluteOuterGeomBounds(DoubleVector.ZERO) }.reduce { r0, r1 ->
            r0.union(r1)
        }
    }

    fun overallTileBounds(plotLayoutInfo: PlotLayoutInfo): DoubleRectangle {
        require(plotLayoutInfo.tiles.isNotEmpty()) { "Plot is empty" }
        return plotLayoutInfo.tiles.map { it.getAbsoluteBounds(DoubleVector.ZERO) }.reduce { r0, r1 ->
            r0.union(r1)
        }
    }

    fun liveMapBounds(container: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            container.origin.add(LIVE_MAP_PLOT_PADDING),
            container.dimension.subtract(LIVE_MAP_PLOT_MARGIN)
        )
    }

    fun subtractTitlesAndLegends(
        baseSize: DoubleVector,
        title: String?,
        subtitle: String?,
        caption: String?,
        hAxisTitle: String?,
        vAxisTitle: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            title,
            subtitle,
            caption,
            hAxisTitle,
            vAxisTitle,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flippedAxis
        )
        val reduced = baseSize.subtract(delta)
        return DoubleVector(
            max(reduced.x, GEOM_MIN_SIZE.x),
            max(reduced.y, GEOM_MIN_SIZE.y)
        )
    }

    fun addTitlesAndLegends(
        base: DoubleVector,
        title: String?,
        subtitle: String?,
        caption: String?,
        hAxisTitle: String?,
        vAxisTitle: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            title,
            subtitle,
            caption,
            hAxisTitle,
            vAxisTitle,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flippedAxis
        )
        return base.add(delta)
    }

    private fun titlesAndLegendsSizeDelta(
        title: String?,
        subtitle: String?,
        caption: String?,
        hAxisTitle: String?,
        vAxisTitle: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val titleDelta = titleSizeDelta(title, subtitle, theme.plot())
        val axisTitlesDelta = axisTitlesSizeDelta(
            hAxisTitleInfo = hAxisTitle to PlotLabelSpecFactory.axisTitle(theme.horizontalAxis(flippedAxis)),
            vAxisTitleInfo = vAxisTitle to PlotLabelSpecFactory.axisTitle(theme.verticalAxis(flippedAxis)),
            axisEnabled,
            marginDimensions = axisMarginDimensions(theme, flippedAxis)
        )
        val legendBlockDelta = legendBlockDelta(legendsBlockInfo, theme.legend())
        val captionDelta = captionSizeDelta(caption, theme.plot())
        return titleDelta.add(axisTitlesDelta).add(legendBlockDelta).add(captionDelta)
    }

    fun titleSizeDelta(title: String?, subtitle: String?, theme: PlotTheme): DoubleVector {
        return DoubleVector(
            0.0,
            titleThickness(title, PlotLabelSpecFactory.plotTitle(theme), theme.titleMargins()) +
                    titleThickness(subtitle, PlotLabelSpecFactory.plotSubtitle(theme), theme.subtitleMargins())
        )
    }

    fun captionSizeDelta(caption: String?, theme: PlotTheme): DoubleVector {
        return DoubleVector(
            0.0,
            titleThickness(caption, PlotLabelSpecFactory.plotCaption(theme), theme.captionMargins())
        )
    }

    fun axisMarginDimensions(theme: Theme, flippedAxis: Boolean): DoubleVector {
        val width = theme.verticalAxis(flippedAxis).titleMargins().width()
        val height = theme.horizontalAxis(flippedAxis).titleMargins().height()
        return DoubleVector(width, height)
    }

    private fun axisTitlesSizeDelta(
        hAxisTitleInfo: Pair<String?, LabelSpec>,
        vAxisTitleInfo: Pair<String?, LabelSpec>,
        axisEnabled: Boolean,
        marginDimensions: DoubleVector
    ): DoubleVector {
        return if (axisEnabled) {
            val hAxisThickness = titleThickness(
                title = hAxisTitleInfo.first,
                labelSpec = hAxisTitleInfo.second,
                margin = marginDimensions.y
            )
            val vAxisThickness = titleThickness(
                title = vAxisTitleInfo.first,
                labelSpec = vAxisTitleInfo.second,
                margin = marginDimensions.x
            )

            DoubleVector(vAxisThickness, hAxisThickness)
        } else {
            DoubleVector.ZERO
        }
    }

    fun axisTitlesOriginOffset(
        hAxisTitleInfo: Pair<String?, LabelSpec>,
        vAxisTitleInfo: Pair<String?, LabelSpec>,
        hasTopAxisTitle: Boolean,
        hasLeftAxisTitle: Boolean,
        axisEnabled: Boolean,
        marginDimensions: DoubleVector
    ): DoubleVector {
        return if (axisEnabled) {
            val yOffset = when (hasTopAxisTitle) {
                true -> titleThickness(
                    title = hAxisTitleInfo.first,
                    labelSpec = hAxisTitleInfo.second,
                    margin = marginDimensions.y
                )

                else -> 0.0
            }

            val xOffset = when (hasLeftAxisTitle) {
                true -> titleThickness(
                    title = vAxisTitleInfo.first,
                    labelSpec = vAxisTitleInfo.second,
                    margin = marginDimensions.x
                )

                else -> 0.0
            }

            DoubleVector(xOffset, yOffset)
        } else {
            DoubleVector.ZERO
        }
    }

    private fun legendBlockDelta(
        legendsBlockInfo: LegendsBlockInfo,
        theme: LegendTheme,
    ): DoubleVector {
        if (!theme.position().isFixed) return DoubleVector.ZERO

        val size = legendsBlockInfo.size()
        return when (theme.position()) {
            LegendPosition.LEFT,
            LegendPosition.RIGHT -> DoubleVector(size.x, 0.0)

            else -> DoubleVector(0.0, size.y)
        }
    }

    fun legendBlockLeftTopDelta(
        legendsBlockInfo: LegendsBlockInfo,
        theme: LegendTheme,
    ): DoubleVector {
        if (!theme.position().isFixed) return DoubleVector.ZERO

        val size = legendsBlockInfo.size()
        return when (theme.position()) {
            LegendPosition.LEFT -> DoubleVector(size.x, 0.0)
            LegendPosition.TOP -> DoubleVector(0.0, size.y)
            else -> DoubleVector.ZERO
        }
    }
}
