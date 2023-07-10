/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil
import jetbrains.datalore.plot.builder.theme.LegendTheme

abstract class ColorBarComponentLayout(
    title: String,
    domain: DoubleSpan,
    breaks: ScaleBreaks,
    protected val guideBarSize: DoubleVector,
    legendDirection: LegendDirection,
    reverse: Boolean,
    theme: LegendTheme
) : LegendBoxLayout(
    title,
    legendDirection,
    theme
) {

    var barBounds: DoubleRectangle private set

    // num of pix added on each end of the bar (to avoid terminal ticks to lay on the border)
    val barLengthExpand: Double = 2.0

    protected abstract val guideBarLength: Double
    internal val breakInfos: List<BreakInfo>

    init {
        val guideBarLength = guideBarLength
        val targetRange = DoubleSpan(0.0 + barLengthExpand, guideBarLength - barLengthExpand)
        val mapper = Mappers.linear(domain, targetRange, reverse)
        breakInfos = breaks.transformedValues.map {
            val tickLocation = mapper(it)!!
            createBreakInfo(tickLocation)
        }
        barBounds = DoubleRectangle(DoubleVector.ZERO, guideBarSize)
    }

    internal abstract fun createBreakInfo(tickLocation: Double): BreakInfo

    internal class BreakInfo(
        val tickLocation: Double,
        val labelLocation: DoubleVector,
        val labelHorizontalAnchor: Text.HorizontalAnchor,
        val labelVerticalAnchor: Text.VerticalAnchor
    )

    private class HorizontalLayout(
        title: String,
        domain: DoubleSpan,
        breaks: ScaleBreaks,
        barSize: DoubleVector,
        reverse: Boolean,
        theme: LegendTheme
    ) : ColorBarComponentLayout(
        title, domain, breaks, barSize,
        LegendDirection.HORIZONTAL,
        reverse,
        theme
    ) {

        override val graphSize: DoubleVector
        private val labelDistance: Double get() = PlotLabelSpecFactory.legendItem(theme).height() / 3
        override val guideBarLength: Double get() = guideBarSize.x

        init {
            // Bar + labels bounds
            val maxLabelHeight = breaks.labels.maxOf { label ->
                PlotLayoutUtil.textDimensions(label, PlotLabelSpecFactory.legendItem(theme)).y
            }
            graphSize = DoubleVector(
                guideBarSize.x,
                guideBarSize.y + labelDistance + maxLabelHeight
            )
        }

        override fun createBreakInfo(tickLocation: Double): BreakInfo {
            val labelLocation = DoubleVector(tickLocation, guideBarSize.y + labelDistance)
            return BreakInfo(
                tickLocation,
                labelLocation,
                Text.HorizontalAnchor.MIDDLE,
                Text.VerticalAnchor.TOP
            )
        }
    }

    private class VerticalLayout(
        title: String,
        domain: DoubleSpan,
        breaks: ScaleBreaks,
        barSize: DoubleVector,
        reverse: Boolean,
        theme: LegendTheme
    ) : ColorBarComponentLayout(
        title, domain, breaks, barSize,
        LegendDirection.VERTICAL,
        reverse,
        theme
    ) {

        override val graphSize: DoubleVector
        private val labelDistance: Double get() = PlotLabelSpecFactory.legendItem(theme).width(PlotLabelSpecFactory.DISTANCE_TO_LABEL_IN_CHARS) / 2
        override val guideBarLength: Double get() = guideBarSize.y

        init {
            check(!breaks.isEmpty) { "Colorbar VerticalLayout received empty breaks list." }
            val maxLabelWidth: Double = breaks.labels
                .maxOf { PlotLabelSpecFactory.legendItem(theme).width(it) }

            // Bar + labels bounds
            graphSize = DoubleVector(guideBarSize.x + labelDistance + maxLabelWidth, guideBarSize.y)
        }

        override fun createBreakInfo(tickLocation: Double): BreakInfo {
            val labelLocation = DoubleVector(guideBarSize.x + labelDistance, tickLocation)
            return BreakInfo(
                tickLocation,
                labelLocation,
                Text.HorizontalAnchor.LEFT,
                Text.VerticalAnchor.CENTER
            )
        }
    }

    companion object {
        fun horizontal(
            title: String,
            domain: DoubleSpan,
            breaks: ScaleBreaks,
            barSize: DoubleVector,
            reverse: Boolean,
            theme: LegendTheme
        ): ColorBarComponentLayout {
            return HorizontalLayout(
                title,
                domain,
                breaks,
                barSize,
                reverse,
                theme
            )
        }

        fun vertical(
            title: String,
            domain: DoubleSpan,
            breaks: ScaleBreaks,
            barSize: DoubleVector,
            reverse: Boolean,
            theme: LegendTheme
        ): ColorBarComponentLayout {
            return VerticalLayout(
                title,
                domain,
                breaks,
                barSize,
                reverse,
                theme
            )
        }
    }
}
