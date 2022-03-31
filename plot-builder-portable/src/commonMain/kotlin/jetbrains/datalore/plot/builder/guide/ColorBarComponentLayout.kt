/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks

abstract class ColorBarComponentLayout(
    title: String,
    domain: DoubleSpan,
    breaks: ScaleBreaks,
    protected val guideBarSize: DoubleVector,
    legendDirection: LegendDirection,
    reverse: Boolean
) : LegendBoxLayout(
    title,
    legendDirection
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
        reverse: Boolean
    ) : ColorBarComponentLayout(
        title, domain, breaks, barSize,
        LegendDirection.HORIZONTAL,
        reverse
    ) {

        override val graphSize: DoubleVector
        private val labelDistance: Double get() = LABEL_SPEC.height() / 3
        override val guideBarLength: Double get() = guideBarSize.x

        init {
            // Bar + labels bounds
            graphSize = DoubleVector(guideBarSize.x, guideBarSize.y + labelDistance + LABEL_SPEC.height())
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
        reverse: Boolean
    ) : ColorBarComponentLayout(
        title, domain, breaks, barSize,
        LegendDirection.VERTICAL,
        reverse
    ) {

        override val graphSize: DoubleVector
        private val labelDistance: Double get() = LABEL_SPEC.width(1) / 2
        override val guideBarLength: Double get() = guideBarSize.y

        init {
            check(!breaks.isEmpty) { "Colorbar VerticalLayout received empty breaks list." }
            val maxLabelWidth: Double = breaks.labels.map { it.length }
                .maxOf { LABEL_SPEC.width(it) }

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
            reverse: Boolean
        ): ColorBarComponentLayout {
            return HorizontalLayout(
                title,
                domain,
                breaks,
                barSize,
                reverse
            )
        }

        fun vertical(
            title: String,
            domain: DoubleSpan,
            breaks: ScaleBreaks,
            barSize: DoubleVector,
            reverse: Boolean
        ): ColorBarComponentLayout {
            return VerticalLayout(
                title,
                domain,
                breaks,
                barSize,
                reverse
            )
        }
    }
}
