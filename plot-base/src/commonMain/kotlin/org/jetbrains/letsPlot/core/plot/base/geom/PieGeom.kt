/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCircleElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import kotlin.math.*

class PieGeom : GeomBase(), WithWidth, WithHeight {
    var holeSize: Double = 0.0
    var spacerWidth: Double = 0.75
    var spacerColor: Color = Color.WHITE
    var strokeSide: StrokeSide = StrokeSide.BOTH
    var sizeUnit: String? = null

    enum class StrokeSide {
        OUTER, INNER, BOTH;

        val hasOuter: Boolean
            get() = this == OUTER || this == BOTH

        val hasInner: Boolean
            get() = this == INNER || this == BOTH
    }

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PieLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.SLICE)
            .groupBy { p -> DoubleVector(p.x()!!, p.y()!!) }
            .forEach { (point, dataPoints) ->
                val sizeUnitRatio = when (sizeUnit) {
                    null -> 1.0
                    else -> getSizeUnitRatio(point, coord, sizeUnit!!)
                }
                val toLocation = { p: DataPointAesthetics -> geomHelper.toClient(point, p) }
                val pieSectors = computeSectors(dataPoints, toLocation, sizeUnitRatio, ctx.backgroundColor)

                root.appendNodes(pieSectors.map(::buildSvgSector))
                root.appendNodes(pieSectors.map(::buildSvgArcs))
                if (spacerWidth > 0) {
                    root.appendNodes(
                        buildSvgSpacerLines(pieSectors, width = spacerWidth, color = spacerColor)
                    )
                }

                pieSectors.forEach { buildHint(it, ctx.targetCollector) }

                ctx.annotations?.let { buildAnnotations(root, pieSectors, ctx) }
            }
    }

    private fun SvgPathDataBuilder.svgOuterArc(sector: Sector) {
        return with(sector) {
            ellipticalArc(
                rx = radius,
                ry = radius,
                xAxisRotation = 0.0,
                largeArc = angle > PI,
                sweep = true,
                to = outerArcEnd
            )
        }
    }

    private fun SvgPathDataBuilder.svgInnerArc(sector: Sector) {
        return with(sector) {
            ellipticalArc(
                rx = holeRadius,
                ry = holeRadius,
                xAxisRotation = 0.0,
                largeArc = angle > PI,
                sweep = false,
                to = innerArcStart
            )
        }
    }

    private fun buildSvgSector(sector: Sector): LinePath {
        return LinePath(
            SvgPathDataBuilder().apply {
                moveTo(sector.innerArcStart)
                lineTo(sector.outerArcStart)
                svgOuterArc(sector)
                lineTo(sector.innerArcEnd)
                svgInnerArc(sector)
            }
        ).apply {
            val fill = sector.p.fill()!!
            val fillAlpha = AestheticsUtil.alpha(fill, sector.p)
            fill().set(Colors.withOpacity(fill, fillAlpha))
        }
    }

    private fun buildSvgArcs(sector: Sector): LinePath {
        return LinePath(
            SvgPathDataBuilder().apply {
                if (strokeSide.hasOuter) {
                    moveTo(sector.outerArcStart)
                    svgOuterArc(sector)
                }
                if (strokeSide.hasInner) {
                    moveTo(sector.innerArcEnd)
                    svgInnerArc(sector)
                }
            }
        ).apply {
            width().set(sector.strokeWidth)
            color().set(sector.p.color())
        }
    }

    private fun buildSvgSpacerLines(pieSectors: List<Sector>, width: Double, color: Color): List<LinePath> {
        fun svgSpacerLines(sector: Sector, atStart: Boolean, atEnd: Boolean): LinePath {
            return LinePath(
                SvgPathDataBuilder().apply {
                    if (atStart) {
                        moveTo(sector.innerStrokeStartPoint)
                        lineTo(sector.outerStrokeStartPoint)
                    }
                    if (atEnd) {
                        moveTo(sector.innerStrokeEndPoint)
                        lineTo(sector.outerStrokeEndPoint)
                    }
                }
            ).apply {
                width().set(width)
                color().set(color)
            }
        }

        // Do not draw spacer lines for exploded sectors and their neighbors

        val explodedSectors = pieSectors.mapIndexedNotNull { index, sector ->
            index.takeIf { sector.position != sector.pieCenter }
        }

        fun needAddAtStart(index: Int) = when (index) {
            in explodedSectors -> false
            0 -> pieSectors.lastIndex !in explodedSectors
            else -> index - 1 !in explodedSectors
        }

        fun needAddAtEnd(index: Int) = when (index) {
            in explodedSectors -> false
            pieSectors.lastIndex -> 0 !in explodedSectors
            else -> index + 1 !in explodedSectors
        }

        return pieSectors.mapIndexed { index, sector ->
            svgSpacerLines(
                sector,
                atStart = needAddAtStart(index),
                atEnd = needAddAtEnd(index)
            )
        }
    }

    private fun buildHint(sector: Sector, targetCollector: GeomTargetCollector) {
        fun resampleArc(outerArc: Boolean): List<DoubleVector> {
            val arcPoint = when (outerArc) {
                true -> { angle: Double -> sector.outerArcPointWithStroke(angle) }
                false -> { angle: Double -> sector.innerArcPointWithStroke(angle) }
            }

            val startPoint = when (outerArc) {
                true -> sector.outerStrokeStartPoint
                false -> sector.innerStrokeStartPoint
            }

            val endPoint = when (outerArc) {
                true -> sector.outerStrokeEndPoint
                false -> sector.innerStrokeEndPoint
            }

            val segmentLength = startPoint.subtract(endPoint).length()

            val arc = { p: DoubleVector ->
                val ratio = p.subtract(startPoint).length() / segmentLength
                if (ratio.isFinite()) {
                    arcPoint(sector.startAngle + sector.angle * ratio)
                } else {
                    p
                }
            }

            return AdaptiveResampler.forDoubleVector(arc, 2.0).resample(startPoint, endPoint)
        }

        targetCollector.addPolygon(
            points = resampleArc(outerArc = true) + resampleArc(outerArc = false).reversed(),
            index = sector.p.index(),
            GeomTargetCollector.TooltipParams(
                markerColors = listOf(
                    HintColorUtil.applyAlpha(sector.p.fill()!!, sector.p.alpha()!!)
                )
            )
        )
    }

    private fun computeSectors(
        dataPoints: List<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?,
        sizeUnitRatio: Double,
        backgroundColor: Color
    ): List<Sector> {
        val sum = dataPoints.sumOf { abs(it.slice()!!) }
        fun angle(p: DataPointAesthetics) = when (sum) {
            0.0 -> 1.0 / dataPoints.size
            else -> abs(p.slice()!!) / sum
        }.let { PI * 2.0 * it }

        // the first slice goes to the left of 12 o'clock and others go clockwise
        var currentAngle = -PI / 2.0
        currentAngle -= angle(dataPoints.first())

        return dataPoints.mapNotNull { p ->
            val pieCenter = toLocation(p) ?: return@mapNotNull null
            Sector(
                p = p,
                pieCenter = pieCenter,
                startAngle = currentAngle,
                endAngle = currentAngle + angle(p),
                sizeUnitRatio = sizeUnitRatio,
                backgroundColor = backgroundColor
            ).also { sector -> currentAngle = sector.endAngle }
        }
    }

    private inner class Sector(
        val pieCenter: DoubleVector,
        val p: DataPointAesthetics,
        val startAngle: Double,
        val endAngle: Double,
        sizeUnitRatio: Double,
        backgroundColor: Color
    ) {
        val angle = endAngle - startAngle
        val strokeWidth = p.stroke() ?: 0.0
        private val hasVisibleStroke = strokeWidth > 0.0 && p.color()?.alpha != 0 && p.color() != backgroundColor
        val radius: Double = sizeUnitRatio * AesScaling.pieDiameter(p) / 2
        val holeRadius = radius * holeSize
        val direction = startAngle + angle / 2
        private val explode = p.explode()?.let { radius * it } ?: 0.0
        val position = pieCenter.add(DoubleVector(explode * cos(direction), explode * sin(direction)))
        private val fullCircleDrawingFix = if (angle % (2 * PI) == 0.0) 0.0001 else 0.0

        val outerArcStart = arcPoint(radius, startAngle)
        val outerArcEnd = arcPoint(radius, endAngle - fullCircleDrawingFix)

        val innerArcStart = arcPoint(holeRadius, startAngle)
        val innerArcEnd = arcPoint(holeRadius, endAngle - fullCircleDrawingFix)

        val outerStrokeStartPoint = outerArcPointWithStroke(startAngle)
        val outerStrokeEndPoint = outerArcPointWithStroke(endAngle - fullCircleDrawingFix)

        val innerStrokeStartPoint = innerArcPointWithStroke(startAngle)
        val innerStrokeEndPoint = innerArcPointWithStroke(endAngle - fullCircleDrawingFix)

        fun outerArcPointWithStroke(angle: Double) = arcPoint(
            radius = when (strokeSide.hasOuter && hasVisibleStroke) {
                true -> radius + strokeWidth / 2
                false -> radius
            },
            angle = angle
        )

        fun innerArcPointWithStroke(angle: Double) = arcPoint(
            radius = when (strokeSide.hasInner && hasVisibleStroke && holeSize > 0 ){
                true -> holeRadius - strokeWidth / 2
                false -> holeRadius
            },
            angle = angle
        )

        private fun arcPoint(radius: Double, angle: Double): DoubleVector {
            return position.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
        }

        val sectorCenter: DoubleVector // center of the pie slice geometry
            get() {
                val offset = holeRadius + 0.5 * (radius - holeRadius)
                return arcPoint(offset, direction)
            }
    }

    private inner class PieLegendKeyElementFactory : LegendKeyElementFactory {
        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            return SvgGElement().apply {
                children().add(
                    SvgCircleElement(
                        size.x / 2,
                        size.y / 2,
                        shapeSize(p) / 2
                    ).apply {
                        fillColor().set(p.fill())
                        strokeColor().set(p.color())
                        strokeWidth().set(p.stroke())
                    }
                )
            }
        }

        override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
            val shapeSize = shapeSize(p)
            val size = shapeSize + 4.0
            return DoubleVector(size, size)
        }

        private fun shapeSize(p: DataPointAesthetics) = AesScaling.pieDiameter(p)
    }

    /// Annotations

    private fun buildAnnotations(
        root: SvgRoot,
        sectors: List<Sector>,
        ctx: GeomContext
    ) {
        if (ctx.annotations == null || sectors.isEmpty()) return

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

        val textSizeGetter: (String, DataPointAesthetics) -> DoubleVector = { text, p ->
            TextUtil.measure(text, toTextDataPointAesthetics(p, ctx.annotations!!.textStyle), ctx)
        }

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
            getAnnotationLabel(sector, ctx.annotations!!, textSizeGetter, offsetForPointer)
        }
        createAnnotationElements(
            pieCenter,
            annotationLabels,
            textStyle = ctx.annotations!!.textStyle,
            xRange = DoubleSpan(leftBorder, rightBorder),
            ctx
        ).forEach(root::add)
    }

    private fun getAnnotationLabel(
        sector: Sector,
        annotations: Annotations,
        textSizeGetter: (String, DataPointAesthetics) -> DoubleVector,
        offsetForPointer: Double
    ): AnnotationLabel {
        val text = annotations.getAnnotationText(sector.p.index())
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

        val pointerLocation = if (canBePlacedInside) {
            sector.sectorCenter
        } else {
            val offset = sector.holeRadius + 0.8 * (sector.radius - sector.holeRadius)
            sector.position.add(DoubleVector(offset * cos(sector.direction), offset * sin(sector.direction)))
            // sector.arcPoint(offset, sector.direction)
        }
        val side = when {
            canBePlacedInside -> Side.INSIDE
            pointerLocation.x < sector.pieCenter.x -> Side.LEFT
            else -> Side.RIGHT
        }
        val outerPointerCoord: DoubleVector? = if (canBePlacedInside) {
            null
        } else {
            sector.position.add(
                DoubleVector(
                    offsetForPointer * cos(sector.direction),
                    offsetForPointer * sin(sector.direction)
                )
            )
        }
        val textColor = when {
            side != Side.INSIDE -> annotations.textStyle.color
            Colors.luminance(sector.p.fill()!!) < 0.5 -> Color.WHITE // if fill is dark
            else -> Color.BLACK
        }
        return AnnotationLabel(
            text,
            textSize,
            pointerLocation,
            outerPointerCoord,
            textColor,
            side
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun getSizeUnitRatio(
            p: DoubleVector,
            coord: CoordinateSystem,
            axis: String
        ): Double {
            val unitSquareSize = coord.unitSize(p)
            val unitSize = when (axis.lowercase()) {
                "x" -> unitSquareSize.x
                "y" -> unitSquareSize.y
                else -> error("Size unit value must be either 'x' or 'y', but was $axis.")
            }
            return unitSize / AesScaling.PIE_UNIT_SIZE
        }

        // For annotations

        private const val INTERVAL_BETWEEN_ANNOTATIONS = 4.0

        private fun toTextDataPointAesthetics(
            p: DataPointAesthetics = AestheticsBuilder().build().dataPointAt(0),
            textStyle: TextStyle,
            color: Color? = null,
            hjust: String? = null
        ): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.SIZE -> textStyle.size / 2
                        Aes.FAMILY -> textStyle.family
                        Aes.FONTFACE -> textStyle.face.toString()
                        Aes.COLOR -> color
                        Aes.HJUST -> hjust ?: "middle"
                        Aes.VJUST -> "center"
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }

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
            val side: Side
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

            return Side.values().flatMap(::createForSide)
        }

        private fun createAnnotationElement(
            label: AnnotationLabel,
            textLocation: DoubleVector,
            textStyle: TextStyle,
            ctx: GeomContext
        ): SvgGElement {

            val g = TextGeom().buildTextComponent(
                toTextDataPointAesthetics(
                    textStyle = textStyle,
                    color = label.textColor,
                    hjust = label.side.getHJust()
                ),
                textLocation,
                label.text,
                sizeUnitRatio = 1.0,
                ctx,
                boundsCenter = null
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

    private fun dimensionSpan(p: DataPointAesthetics, coordAes: Aes<Double>): DoubleSpan? {
        val loc = p[coordAes]
        val size = p[Aes.SIZE]
        return if (SeriesUtil.allFinite(loc, size)) {
            loc!!
            val expand = size!! / 2.0
            DoubleSpan(
                loc - expand,
                loc + expand
            )
        } else {
            null
        }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        if (!isDiscrete) return null
        return dimensionSpan(p, coordAes)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        if (!isDiscrete) return null
        return dimensionSpan(p, coordAes)
    }
}
