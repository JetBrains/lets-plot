/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.Theme
import kotlin.math.max

internal object PlotLayoutUtil {
    private const val AXIS_TITLE_OUTER_MARGIN = 4.0
    const val AXIS_TITLE_INNER_MARGIN = 4.0
    private const val TITLE_V_MARGIN = 4.0
    private val LIVE_MAP_PLOT_PADDING = DoubleVector(10.0, 0.0)
    private val LIVE_MAP_PLOT_MARGIN = DoubleVector(10.0, 10.0)

    private fun labelDimensions(text: String, labelSpec: LabelSpec): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector(0.0, labelSpec.height())
        }
        return DoubleVector(
            labelSpec.width(text.length),
            labelSpec.height()
        )
    }

    internal fun textLinesDimensions(textLines: List<String>, labelSpec: LabelSpec): List<DoubleVector> {
        return textLines.map { line -> labelDimensions(line, labelSpec) }
    }

    internal fun textDimensions(textLines: List<String>, labelSpec: LabelSpec): DoubleVector {
        val linesDimensions = textLinesDimensions(textLines, labelSpec)
        if (linesDimensions.isEmpty()) {
            return DoubleVector.ZERO
        }

        fun DoubleVector.union(p: DoubleVector): DoubleVector {
            return DoubleVector(
                x = max(x, p.x),
                y = y + p.y
            )
        }
        return linesDimensions
            .fold(DoubleVector.ZERO) { acc, dv -> acc.union(dv) }
    }

    internal fun titleDimensions(textLines: List<String>, labelSpec: LabelSpec): DoubleVector {
        return if (textLines.isEmpty()) {
            DoubleVector.ZERO
        } else {
            textDimensions(textLines, labelSpec).add(DoubleVector(0.0, 2 * TITLE_V_MARGIN))
        }
    }

    private fun axisTitleDimensions(text: String) = labelDimensions(text, PlotLabelSpec.AXIS_TITLE)

    fun overallGeomBounds(plotLayoutInfo: PlotLayoutInfo): DoubleRectangle {
        require(plotLayoutInfo.tiles.isNotEmpty()) { "Plot is empty" }
        return plotLayoutInfo.tiles.map { it.getAbsoluteGeomBounds(DoubleVector.ZERO) }.reduce { r0, r1 ->
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
        titleLines: List<String>,
        subtitleLines: List<String>,
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        captionLines: List<String>
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            titleLines,
            subtitleLines,
            axisTitleLeft,
            axisTitleBottom,
            axisEnabled,
            legendsBlockInfo,
            theme,
            captionLines
        )
        val reduced = baseSize.subtract(delta)
        return DoubleVector(
            max(reduced.x, TileLayoutUtil.GEOM_MIN_SIZE.x),
            max(reduced.y, TileLayoutUtil.GEOM_MIN_SIZE.y)
        )
    }

    fun addTitlesAndLegends(
        base: DoubleVector,
        titleLines: List<String>,
        subtitleLines: List<String>,
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        captionLines: List<String>
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            titleLines,
            subtitleLines,
            axisTitleLeft,
            axisTitleBottom,
            axisEnabled,
            legendsBlockInfo,
            theme,
            captionLines
        )
        return base.add(delta)
    }

    private fun titlesAndLegendsSizeDelta(
        titleLines: List<String>,
        subtitleLines: List<String>,
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        captionLines: List<String>,
    ): DoubleVector {
        val titleDelta = titleSizeDelta(titleLines, subtitleLines)
        val axisTitlesDelta = axisTitleSizeDelta(axisTitleLeft, axisTitleBottom, axisEnabled)
        val legendBlockDelta = legendBlockDelta(legendsBlockInfo, theme.legend())
        val captionDelta = DoubleVector(0.0, titleDimensions(captionLines, PlotLabelSpec.PLOT_CAPTION).y)
        return titleDelta.add(axisTitlesDelta).add(legendBlockDelta).add(captionDelta)
    }

    fun titleSizeDelta(titleLines: List<String>, subtitleLines: List<String>): DoubleVector {
        return DoubleVector(
            0.0,
            titleDimensions(titleLines, PlotLabelSpec.PLOT_TITLE).y +
                    titleDimensions(subtitleLines, PlotLabelSpec.PLOT_SUBTITLE).y
        )
    }

    fun axisTitleSizeDelta(
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean
    ): DoubleVector {
        if (!axisEnabled) return DoubleVector.ZERO

        val axisTitleLeftDelta = DoubleVector(axisTitleThickness(axisTitleLeft), 0.0)
        val axisTitleBottomDelta = DoubleVector(0.0, axisTitleThickness(axisTitleBottom))

        return axisTitleLeftDelta.add(axisTitleBottomDelta)
    }

    private fun axisTitleThickness(title: String?): Double {
        if (title == null) return 0.0
        val titleSize = axisTitleDimensions(title)
        return titleSize.y + AXIS_TITLE_OUTER_MARGIN + AXIS_TITLE_INNER_MARGIN
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
