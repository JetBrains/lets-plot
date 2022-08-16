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
import jetbrains.datalore.plot.builder.theme.PlotTheme
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

    internal fun textLinesDimensions(text: String, labelSpec: LabelSpec): List<DoubleVector> {
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

    internal fun titleDimensions(title: String?, labelSpec: LabelSpec): DoubleVector {
        return if (title == null) {
            DoubleVector.ZERO
        } else {
            textDimensions(title, labelSpec).add(DoubleVector(0.0, 2 * TITLE_V_MARGIN))
        }
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
            axisEnabled
        )
        val legendBlockDelta = legendBlockDelta(legendsBlockInfo, theme.legend())
        val captionDelta = captionSizeDelta(caption, theme.plot())
        return titleDelta.add(axisTitlesDelta).add(legendBlockDelta).add(captionDelta)
    }

    fun titleSizeDelta(title: String?, subtitle: String?, theme: PlotTheme): DoubleVector {
        return DoubleVector(
            0.0,
            titleDimensions(title, PlotLabelSpecFactory.plotTitle(theme)).y +
                    titleDimensions(subtitle, PlotLabelSpecFactory.plotSubtitle(theme)).y
        )
    }

    fun captionSizeDelta(caption: String?, theme: PlotTheme): DoubleVector {
        return DoubleVector(0.0, titleDimensions(caption, PlotLabelSpecFactory.plotCaption(theme)).y)
    }

    fun axisTitleSizeDelta(
        axisTitleLeft: Pair<String?, PlotLabelSpec>,
        axisTitleBottom: Pair<String?, PlotLabelSpec>,
        axisEnabled: Boolean
    ): DoubleVector {
        if (!axisEnabled) return DoubleVector.ZERO

        val axisTitleLeftDelta = DoubleVector(
            axisTitleThickness(title = axisTitleLeft.first, axisTitleLabelSpec = axisTitleLeft.second),
            0.0
        )
        val axisTitleBottomDelta = DoubleVector(
            0.0,
            axisTitleThickness(title = axisTitleBottom.first, axisTitleLabelSpec = axisTitleBottom.second)
        )

        return axisTitleLeftDelta.add(axisTitleBottomDelta)
    }

    private fun axisTitleThickness(title: String?, axisTitleLabelSpec: PlotLabelSpec): Double {
        if (title == null) return 0.0
        val titleSize = textDimensions(title, axisTitleLabelSpec)
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
