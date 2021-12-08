/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement
import kotlin.math.max

class ColorBarComponent(
    override val spec: ColorBarComponentSpec
) : LegendBox() {

    override fun appendGuideContent(contentRoot: SvgNode): DoubleVector {
        val layout = spec.layout

        val guideBarGroup = SvgGElement()

        // bar
        val barBounds = layout.barBounds
        val horizontal = layout.isHorizontal
        addColorBar(
            guideBarGroup,
            spec.domain, spec.scale, spec.binCount, barBounds,
            layout.barLengthExpand,
            horizontal, spec.reverse
        )

        // Ticks and labels
        val barThickness = when {
            horizontal -> barBounds.height
            else -> barBounds.width
        }
        val tickLength = barThickness / 5

        val breakInfos = layout.breakInfos.iterator()
        for (brLabel in spec.breaks.labels) {
            val brInfo = breakInfos.next()

            val tickLocation = brInfo.tickLocation
            val tickMarkPoints = ArrayList<DoubleVector>()
            if (horizontal) {
                val tickX = barBounds.left + tickLocation
                tickMarkPoints.add(DoubleVector(tickX, barBounds.top))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.top + tickLength))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.bottom - tickLength))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.bottom))
            } else {
                val tickY = barBounds.top + tickLocation
                tickMarkPoints.add(DoubleVector(barBounds.left, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.left + tickLength, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.right - tickLength, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.right, tickY))
            }

            addTickMark(guideBarGroup, tickMarkPoints[0], tickMarkPoints[1])
            addTickMark(guideBarGroup, tickMarkPoints[2], tickMarkPoints[3])

            val label = TextLabel(brLabel)
            label.textColor().set(theme.textColor())
            label.setHorizontalAnchor(brInfo.labelHorizontalAnchor)
            label.setVerticalAnchor(brInfo.labelVerticalAnchor)
            label.moveTo(brInfo.labelLocation.x, brInfo.labelLocation.y + barBounds.top)
            guideBarGroup.children().add(label.rootGroup)
        }

        if (debug) {
            // frame bar and labels
            val graphBounds = DoubleRectangle(DoubleVector.ZERO, layout.graphSize)
            guideBarGroup.children().add(
                createTransparentRect(
                    graphBounds,
                    Color.DARK_BLUE,
                    1.0
                )
            )
        }

        contentRoot.children().add(guideBarGroup)
        return layout.size
    }

    private fun addColorBar(
        g: SvgGElement,
        domain: ClosedRange<Double>,
        scale: Scale<Color>,
        numBins: Int,
        barBounds: DoubleRectangle,
        barLengthExpand: Double,
        horizontal: Boolean,
        reverse: Boolean
    ) {

        val domainSpan = SeriesUtil.span(domain)
        val stepCount = max(2, numBins)
        val step = domainSpan / stepCount
        val v = domain.lowerEnd + step / 2
        val domainValues = ArrayList<Double>()
        for (i in 0 until stepCount) {
            domainValues.add(v + step * i)
        }
        if (reverse) {
            domainValues.reverse()
        }

        val colors = ScaleUtil.map(domainValues, scale)
        val barLength = when {
            horizontal -> barBounds.width
            else -> barBounds.height
        }
        val effectiveBarLength = barLength - barLengthExpand * 2
        val segmentStep = effectiveBarLength / stepCount

        var segmentLeft = barBounds.left
        val segmentRight = barBounds.right
        val segmentBottom = barBounds.bottom
        var segmentTop = barBounds.top

        for ((i, color) in colors.withIndex()) {
            val r = SvgRectElement(
                segmentLeft,
                segmentTop,
                segmentRight - segmentLeft,
                segmentBottom - segmentTop
            )
            r.strokeWidth().set(0.0)
            r.fillColor().set(color)
            g.children().add(r)

            if (horizontal) {
                segmentLeft += segmentStep
            } else {
                segmentTop += segmentStep
            }
            if (i == 0) {
                // first segment is a bit longer.
                if (horizontal) {
                    segmentLeft += barLengthExpand
                } else {
                    segmentTop += barLengthExpand
                }
            }
        }
    }

    private fun addTickMark(g: SvgGElement, p0: DoubleVector, p1: DoubleVector) {
        val line = SvgLineElement(p0.x, p0.y, p1.x, p1.y)
        line.strokeWidth().set(1.0)
        line.strokeColor().set(theme.backgroundFill());
        g.children().add(line)
    }
}
