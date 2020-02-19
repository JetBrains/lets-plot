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

class ColorBarComponent(spec: ColorBarComponentSpec) : LegendBox(spec) {

    override val spec: ColorBarComponentSpec
        get() {
            return super.spec as ColorBarComponentSpec
        }

    override fun appendGuideContent(contentRoot: SvgNode): DoubleVector {
        val spec = spec
        val l = spec.layout

        // ---------
        val guideBarGroup = SvgGElement()

        // bar
        val barBounds = l.barBounds
        addColorBar(guideBarGroup, spec.domain, spec.scale, spec.binCount, barBounds, l.barLengthExpand, l.isHorizontal)

        // Ticks and labels
        val barThickness = if (l.isHorizontal)
            barBounds.height
        else
            barBounds.width
        val tickLength = barThickness / 5

        val breakInfos = l.breakInfos.iterator()
        for (br in spec.breaks) {
            val brInfo = breakInfos.next()

            val tickLocation = brInfo.tickLocation

            val tickMarkPoints = ArrayList<DoubleVector>()
            if (l.isHorizontal) {
                val tickX = tickLocation + barBounds.left
                tickMarkPoints.add(DoubleVector(tickX, barBounds.top))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.top + tickLength))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.bottom - tickLength))
                tickMarkPoints.add(DoubleVector(tickX, barBounds.bottom))
            } else {
                val tickY = tickLocation + barBounds.top
                tickMarkPoints.add(DoubleVector(barBounds.left, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.left + tickLength, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.right - tickLength, tickY))
                tickMarkPoints.add(DoubleVector(barBounds.right, tickY))
            }

            addTickMark(guideBarGroup, tickMarkPoints[0], tickMarkPoints[1])
            addTickMark(guideBarGroup, tickMarkPoints[2], tickMarkPoints[3])

            val label = TextLabel(br.label)
            label.setHorizontalAnchor(brInfo.labelHorizontalAnchor)
            label.setVerticalAnchor(brInfo.labelVerticalAnchor)
            label.moveTo(brInfo.labelLocation.x, brInfo.labelLocation.y + barBounds.top)
            guideBarGroup.children().add(label.rootGroup)
        }

        // add white frame
        guideBarGroup.children().add(
            createBorder(
                barBounds,
                spec.theme.backgroundFill(),
                1.0
            )
        )

        if (debug) {
            // frame bar and labels
            val graphBounds = DoubleRectangle(DoubleVector.ZERO, l.graphSize)
            guideBarGroup.children().add(
                createBorder(
                    graphBounds,
                    Color.DARK_BLUE,
                    1.0
                )
            )
        }

        contentRoot.children().add(guideBarGroup)
        return l.size
    }

    private fun addColorBar(
        g: SvgGElement,
        domain: ClosedRange<Double>,
        scale: Scale<Color>,
        numBins: Int,
        barBounds: DoubleRectangle,
        barLengthExpand: Double,
        horizontal: Boolean
    ) {

        val domainSpan = SeriesUtil.span(domain)
        val stepCount = max(2, numBins)
        val step = domainSpan / stepCount
        val v = domain.lowerEndpoint() + step / 2
        val domainValues = ArrayList<Double>()
        for (i in 0 until stepCount) {
            domainValues.add(v + step * i)
        }

        val colors = ScaleUtil.map(domainValues, scale)

        val barLength = if (horizontal)
            barBounds.width
        else
            barBounds.height
        val effectiveBarLength = barLength - barLengthExpand * 2
        val segmentStep = effectiveBarLength / stepCount

        var segmentLeft = barBounds.left
        val segmentRight = barBounds.right
        val segmentBottom = barBounds.bottom
        var segmentTop = barBounds.top

        var isFirst = true
        for (color in colors) {
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
            if (isFirst) {
                // first segment was longer
                if (horizontal) {
                    segmentLeft += barLengthExpand
                } else {
                    segmentTop += barLengthExpand
                }
                isFirst = false
            }
        }
    }

    private fun addTickMark(g: SvgGElement, p0: DoubleVector, p1: DoubleVector) {
        val line = SvgLineElement(p0.x, p0.y, p1.x, p1.y)
        line.strokeWidth().set(1.0)
        line.strokeColor().set(spec.theme.backgroundFill());
        g.children().add(line)
    }
}
