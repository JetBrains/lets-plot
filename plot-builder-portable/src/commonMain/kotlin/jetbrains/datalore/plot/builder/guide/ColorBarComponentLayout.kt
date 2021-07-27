/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.scale.GuideBreak

abstract class ColorBarComponentLayout(
    title: String,
    domain: ClosedRange<Double>,
    breaks: List<GuideBreak<Double>>,
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
        val targetRange = ClosedRange(0.0 + barLengthExpand, guideBarLength - barLengthExpand)
        val mapper = Mappers.linear(domain, targetRange, reverse)
        breakInfos = breaks.map {
            val tickLocation = mapper(it.domainValue)
            createBreakInfo(tickLocation)
        }
        barBounds = DoubleRectangle(DoubleVector.ZERO, guideBarSize)
    }

    internal abstract fun createBreakInfo(tickLocation: Double): BreakInfo

    internal class BreakInfo(
        val tickLocation: Double, val labelLocation: DoubleVector,
        val labelHorizontalAnchor: TextLabel.HorizontalAnchor, val labelVerticalAnchor: TextLabel.VerticalAnchor
    )

    private class HorizontalLayout(
        title: String,
        domain: ClosedRange<Double>,
        breaks: List<GuideBreak<Double>>,
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
                TextLabel.HorizontalAnchor.MIDDLE,
                TextLabel.VerticalAnchor.TOP
            )
        }
    }

    private class VerticalLayout(
        title: String,
        domain: ClosedRange<Double>,
        breaks: List<GuideBreak<Double>>,
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
            check(breaks.isNotEmpty()) { "Colorbar VerticalLayout received empty breaks list." }
            val maxLabelWidth: Double = breaks.map { it.label.length }
                .maxOf { LABEL_SPEC.width(it) }

            // Bar + labels bounds
            graphSize = DoubleVector(guideBarSize.x + labelDistance + maxLabelWidth, guideBarSize.y)
        }

        override fun createBreakInfo(tickLocation: Double): BreakInfo {
            val labelLocation = DoubleVector(guideBarSize.x + labelDistance, tickLocation)
            return BreakInfo(
                tickLocation,
                labelLocation,
                TextLabel.HorizontalAnchor.LEFT,
                TextLabel.VerticalAnchor.CENTER
            )
        }
    }

    companion object {
        fun horizontal(
            title: String,
            domain: ClosedRange<Double>,
            breaks: List<GuideBreak<Double>>,
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
            domain: ClosedRange<Double>,
            breaks: List<GuideBreak<Double>>,
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
