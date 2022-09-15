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
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.PlotTheme
import jetbrains.datalore.plot.builder.theme.Theme
import kotlin.math.max

internal object PlotLayoutUtil {
    private val LIVE_MAP_PLOT_PADDING = DoubleVector(10.0, 0.0)
    private val LIVE_MAP_PLOT_MARGIN = DoubleVector(10.0, 10.0)

    private fun labelDimensions(text: String, labelSpec: LabelSpec): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector(0.0, labelSpec.height())
        }
        return DoubleVector(
            labelSpec.width(text),
            labelSpec.height()
        )
    }

    private fun textLinesDimensions(text: String, labelSpec: LabelSpec): List<DoubleVector> {
        return text.split('\n').map(String::trim).map { line -> labelDimensions(line, labelSpec) }
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
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            title,
            subtitle,
            caption,
            axisTitleLeft,
            axisTitleBottom,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flippedAxis
        )
        val reduced = baseSize.subtract(delta)
        return DoubleVector(
            max(reduced.x, TileLayoutUtil.GEOM_MIN_SIZE.x),
            max(reduced.y, TileLayoutUtil.GEOM_MIN_SIZE.y)
        )
    }

    fun addTitlesAndLegends(
        base: DoubleVector,
        title: String?,
        subtitle: String?,
        caption: String?,
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val delta = titlesAndLegendsSizeDelta(
            title,
            subtitle,
            caption,
            axisTitleLeft,
            axisTitleBottom,
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
        axisTitleLeft: String?,
        axisTitleBottom: String?,
        axisEnabled: Boolean,
        legendsBlockInfo: LegendsBlockInfo,
        theme: Theme,
        flippedAxis: Boolean
    ): DoubleVector {
        val titleDelta = titleSizeDelta(title, subtitle, theme.plot())
        val axisTitlesDelta = axisTitleSizeDelta(
            axisTitleLeft to PlotLabelSpecFactory.axisTitle(theme.verticalAxis(flippedAxis)),
            axisTitleBottom to PlotLabelSpecFactory.axisTitle(theme.horizontalAxis(flippedAxis)),
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

    fun axisTitleSizeDelta(
        axisTitleLeft: Pair<String?, LabelSpec>,
        axisTitleBottom: Pair<String?, LabelSpec>,
        axisEnabled: Boolean,
        marginDimensions: DoubleVector
    ): DoubleVector {
        if (!axisEnabled) return DoubleVector.ZERO

        val axisTitleLeftDelta = DoubleVector(
            titleThickness(title = axisTitleLeft.first, labelSpec = axisTitleLeft.second, margin = marginDimensions.x),
            0.0
        )
        val axisTitleBottomDelta = DoubleVector(
            0.0,
            titleThickness(
                title = axisTitleBottom.first,
                labelSpec = axisTitleBottom.second,
                margin = marginDimensions.y
            )
        )

        return axisTitleLeftDelta.add(axisTitleBottomDelta)
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
