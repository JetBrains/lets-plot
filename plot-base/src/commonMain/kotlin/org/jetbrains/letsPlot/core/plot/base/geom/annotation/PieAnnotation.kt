/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.geom.PieGeom.Sector
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCircleElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import kotlin.math.*

object PieAnnotation {

    fun build(
        root: SvgRoot,
        sectors: List<Sector>,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return
        if (sectors.isEmpty()) return

        val pieCenter = sectors.map(Sector::pieCenter).distinct().singleOrNull() ?: return

        // split sectors into left and right...
        val leftSectors = sectors
            .filter { it.outerArcStart.x < pieCenter.x || it.outerArcEnd.x < pieCenter.x || it.sectorCenter.x < pieCenter.x }
            .ifEmpty { sectors }
        val rightSectors = sectors
            .filter { it.outerArcStart.x > pieCenter.x || it.outerArcEnd.x > pieCenter.x || it.sectorCenter.x > pieCenter.x }
            .ifEmpty { sectors }

        val expand = 20.0
        val leftBorder = leftSectors.minOf { it.pieCenter.x - it.radius } - expand
        val rightBorder = rightSectors.maxOf { it.pieCenter.x + it.radius } + expand

        // Use max radius of the largest sector on a given side
        val leftMaxOffsetForOuter =
            leftSectors.maxBy(Sector::radius).let { it.holeRadius + 1.2 * (it.radius - it.holeRadius) }
        val rightMaxOffsetForOuter =
            rightSectors.maxBy(Sector::radius).let { it.holeRadius + 1.2 * (it.radius - it.holeRadius) }
        val annotationLabels = sectors.map { sector ->
            val offsetForPointer = when {
                sector in leftSectors && sector in rightSectors -> max(leftMaxOffsetForOuter, rightMaxOffsetForOuter)
                sector in leftSectors -> leftMaxOffsetForOuter
                else -> rightMaxOffsetForOuter
            }
            getAnnotationLabel(
                sector,
                annotation,
                AnnotationUtil.textSizeGetter(annotation.textStyle, ctx),
                offsetForPointer,
                ctx.plotContext
            )
        }
        createAnnotationElements(
            pieCenter,
            annotationLabels,
            textStyle = annotation.textStyle,
            xRange = DoubleSpan(leftBorder, rightBorder),
            ctx
        ).forEach(root::add)
    }

    private fun getAnnotationLabel(
        sector: Sector,
        annotation: Annotation,
        textSizeGetter: (String, DataPointAesthetics) -> DoubleVector,
        offsetForPointer: Double,
        plotContext: PlotContext?
    ): AnnotationLabel {
        val text = annotation.getAnnotationText(sector.p.index(), plotContext)
        val textSize = textSizeGetter(text, sector.p)

        fun isPointInsideSector(pnt: DoubleVector): Boolean {
            val v = pnt.subtract(sector.position)
            if (v.length() !in sector.holeRadius..sector.radius) {
                return false
            }
            val angle = atan2(v.y, v.x).let {
                when {
                    it in -PI / 2..PI && abs(sector.startAngle) > PI -> it - 2 * PI
                    it in -PI..-PI / 2 && abs(sector.endAngle) > PI -> it + 2 * PI
                    else -> it
                }
            }
            return sector.startAngle <= angle && angle < sector.endAngle
        }

        val textRect = DoubleRectangle(sector.sectorCenter.subtract(textSize.mul(0.5)), textSize)
        val canBePlacedInside =
            textRect.parts.flatMap { listOf(it.start, it.end) }.distinct().all(::isPointInsideSector)

        fun canBePlacedInsideRotated(): Boolean {
            if (textSize.x > sector.radius - sector.holeRadius) {
                return false
            }

            fun checkChord(p1: DoubleVector, p2: DoubleVector, h: Double) = p1.subtract(p2).length() < h

            // outer
            if (checkChord(sector.outerArcStart, sector.outerArcEnd, textSize.y)) {
                return false
            }
            // inner
            val offset = sector.radius - textSize.x
            val onStartArc = sector.arcPoint(offset, sector.startAngle)
            val onEndArc = sector.arcPoint(offset, sector.endAngle)
            return !checkChord(onStartArc, onEndArc, textSize.y)
        }

        val canBePlacedInsideWithRotation = if (!canBePlacedInside) canBePlacedInsideRotated() else false

        val location = if (canBePlacedInside) {
            sector.sectorCenter
        } else if (canBePlacedInsideWithRotation) {
            val offset = sector.radius - textSize.x / 2
            sector.arcPoint(offset, sector.direction)
        } else {
            val offset = sector.holeRadius + 0.8 * (sector.radius - sector.holeRadius)
            sector.arcPoint(offset, sector.direction)
        }
        val side = when {
            canBePlacedInside || canBePlacedInsideWithRotation -> Side.INSIDE
            location.x < sector.pieCenter.x -> Side.LEFT
            else -> Side.RIGHT
        }
        val outerPointerCoord: DoubleVector? = if (side == Side.INSIDE) {
            null
        } else {
            sector.position.add(
                DoubleVector(
                    offsetForPointer * cos(sector.direction),
                    offsetForPointer * sin(sector.direction)
                )
            )
        }
        val rotationAngle = if (canBePlacedInsideWithRotation) {
            toDegrees(-sector.direction) + when {
                location.x < sector.pieCenter.x -> 180.0
                else -> 0.0
            }
        } else {
            0.0
        }
        val textColor = when (side) {
            Side.INSIDE -> annotation.getTextColor(sector.p.fill())
            else -> annotation.getTextColor()
        }
        return AnnotationLabel(
            text,
            textSize,
            location,
            outerPointerCoord,
            textColor,
            side,
            rotationAngle
        )
    }


    private const val INTERVAL_BETWEEN_ANNOTATIONS = 4.0

    /// side around pie to place annotation label
    private enum class Side {
        INSIDE {
            override fun getHJust() = "middle"
        },
        LEFT {
            override fun getHJust() = "right"
        },
        RIGHT {
            override fun getHJust() = "left"
        };

        abstract fun getHJust(): String
    }

    private data class AnnotationLabel(
        val text: String,
        val textSize: DoubleVector,
        val location: DoubleVector,             // to place text element or pointer
        val outerPointerCoord: DoubleVector?,   // position for middle point of pointer line
        val textColor: Color,
        val side: Side,
        val rotationAngle: Double
    )

    private fun createAnnotationElements(
        pieCenter: DoubleVector,
        annotationLabels: List<AnnotationLabel>,
        textStyle: TextStyle,
        xRange: DoubleSpan,
        ctx: GeomContext
    ): List<SvgGElement> {

        fun createForSide(side: Side): List<SvgGElement> {
            if (side == Side.INSIDE) {
                return annotationLabels
                    .filter { it.side == side }
                    .map { createAnnotationElement(label = it, textLocation = it.location, textStyle, ctx) }
            }

            val startFromTheTop: Boolean
            val outsideLabels = annotationLabels.filter { it.side == side }.let { l ->
                // if top y position is in the bottom side => start from the bottom
                startFromTheTop = l.minOfOrNull { it.location.y }?.let { it < pieCenter.y } ?: false
                if (startFromTheTop) {
                    l.sortedBy { it.location.y }
                } else {
                    l.sortedByDescending { it.location.y }
                }
            }

            if (outsideLabels.isEmpty()) {
                return emptyList()
            }

            val startPosition = DoubleVector(
                if (side == Side.LEFT) xRange.lowerEnd else xRange.upperEnd,
                outsideLabels.first().outerPointerCoord!!.y
            )

            var yOffset = 0.0
            return outsideLabels.map { label ->
                val loc = if (startFromTheTop) {
                    DoubleVector(startPosition.x, startPosition.y + yOffset)
                } else {
                    DoubleVector(startPosition.x, startPosition.y - yOffset)
                }
                yOffset += label.textSize.y + INTERVAL_BETWEEN_ANNOTATIONS

                createAnnotationElement(label, loc, textStyle, ctx)
            }
        }

        return Side.entries.flatMap(::createForSide)
    }

    private fun createAnnotationElement(
        label: AnnotationLabel,
        textLocation: DoubleVector,
        textStyle: TextStyle,
        ctx: GeomContext
    ): SvgGElement {
        val g = AnnotationUtil.createTextElement(
            label.text,
            textLocation,
            AnnotationUtil.TextParams(
                style = textStyle,
                color = label.textColor,
                hjust = label.side.getHJust(),
                angle = label.rotationAngle
            ),
            geomContext = ctx,
        )
        if (label.outerPointerCoord == null) return g

        // Add pointer line

        // add offset - stop line before text
        val startXPos = if (label.side == Side.LEFT) {
            textLocation.x + 5.0
        } else {
            textLocation.x - 5.0
        }

        val midXPos = if ((label.side == Side.RIGHT && label.outerPointerCoord.x > startXPos) ||
            (label.side == Side.LEFT && label.outerPointerCoord.x < startXPos)
        ) {
            startXPos
        } else {
            label.outerPointerCoord.x
        }
        val middlePoint = DoubleVector(midXPos, textLocation.y)

        listOf(
            SvgLineElement(startXPos, textLocation.y, middlePoint.x, middlePoint.y),
            SvgLineElement(middlePoint.x, middlePoint.y, label.location.x, label.location.y),
        ).forEach { line ->
            line.strokeColor().set(label.textColor)
            line.strokeWidth().set(0.7)
            g.children().add(line)
        }

        g.children().add(
            SvgCircleElement(label.location.x, label.location.y, 1.5).apply {
                fillColor().set(label.textColor)
            }
        )
        return g
    }
}