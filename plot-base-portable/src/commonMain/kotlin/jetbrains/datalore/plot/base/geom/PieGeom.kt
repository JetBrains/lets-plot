/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.algorithms.AdaptiveResampler
import jetbrains.datalore.base.collections.filterNotNullKeys
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.geom.util.DataPointAestheticsDelegate
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.TextUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.TextStyle
import jetbrains.datalore.vis.svg.SvgCircleElement
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class PieGeom : GeomBase(), WithWidth, WithHeight {
    var holeSize: Double = 0.0
    var strokeWidth: Double = 0.0
    var strokeColor: Color = Color.WHITE
    var fillWithColor: Boolean = false

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
            .groupBy { p -> geomHelper.toClient(p.x()!!, p.y()!!, p) }
            .filterNotNullKeys()
            .forEach { (pieCenter, dataPoints) ->
                val pieSectors = computeSectors(pieCenter, dataPoints)
                appendNodes(pieSectors.map(::buildSvgSector), root)
                pieSectors.forEach { buildHint(it, ctx.targetCollector) }

                ctx.annotations?.let { buildAnnotations(root, pieCenter, pieSectors, ctx) }
            }
    }

    private fun buildSvgSector(sector: Sector): LinePath {
        return LinePath(
            SvgPathDataBuilder().apply {
                moveTo(sector.innerArcStart)
                lineTo(sector.outerArcStart)
                ellipticalArc(
                    rx = sector.radius,
                    ry = sector.radius,
                    xAxisRotation = 0.0,
                    largeArc = sector.angle > PI,
                    sweep = true,
                    to = sector.outerArcEnd
                )
                lineTo(sector.innerArcEnd)
                ellipticalArc(
                    rx = sector.holeRadius,
                    ry = sector.holeRadius,
                    xAxisRotation = 0.0,
                    largeArc = sector.angle > PI,
                    sweep = false,
                    to = sector.innerArcStart
                )
            }
        ).apply {
            val fill = getFillColor(sector.p)
            val fillAlpha = AestheticsUtil.alpha(fill, sector.p)
            fill().set(Colors.withOpacity(fill, fillAlpha))
            width().set(strokeWidth)
            color().set(strokeColor)
        }
    }

    private fun buildHint(sector: Sector, targetCollector: GeomTargetCollector) {
        fun resampleArc(outerArc: Boolean): List<DoubleVector> {
            val arcPoint = when (outerArc) {
                true -> sector::outerArcPoint
                false -> sector::innerArcPoint
            }

            val startPoint = when (outerArc) {
                true -> sector.outerArcStart
                false -> sector.innerArcStart
            }

            val endPoint = when (outerArc) {
                true -> sector.outerArcEnd
                false -> sector.innerArcEnd
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
            localToGlobalIndex = { sector.p.index() },
            GeomTargetCollector.TooltipParams(markerColors = listOf(getFillColor(sector.p)))
        )
    }

    private fun getFillColor(p: DataPointAesthetics) = when (fillWithColor) {
        true -> p.color()!!
        false -> p.fill()!!
    }

    private fun computeSectors(pieCenter: DoubleVector, dataPoints: List<DataPointAesthetics>): List<Sector> {
        val sum = dataPoints.sumOf { abs(it.slice()!!) }
        fun angle(p: DataPointAesthetics) = when (sum) {
            0.0 -> 1.0 / dataPoints.size
            else -> abs(p.slice()!!) / sum
        }.let { PI * 2.0 * it }

        // the first slice goes to the left of 12 o'clock and others go clockwise
        var currentAngle = -PI / 2.0
        currentAngle -= angle(dataPoints.first())

        return dataPoints.map { p ->
            Sector(
                p = p,
                pieCenter = pieCenter,
                startAngle = currentAngle,
                endAngle = currentAngle + angle(p)
            ).also { sector -> currentAngle = sector.endAngle }
        }
    }

    private inner class Sector(
        val pieCenter: DoubleVector,
        val p: DataPointAesthetics,
        val startAngle: Double,
        val endAngle: Double
    ) {
        val angle = endAngle - startAngle
        val radius: Double = AesScaling.pieDiameter(p) / 2
        val holeRadius = radius * holeSize
        val direction = startAngle + angle / 2
        private val explode = radius * p.explode()!!
        val position = pieCenter.add(DoubleVector(explode * cos(direction), explode * sin(direction)))
        private val fullCircleDrawingFix = if (angle % (2 * PI) == 0.0) 0.0001 else 0.0

        val outerArcStart = outerArcPoint(startAngle)
        val outerArcEnd = outerArcPoint(endAngle - fullCircleDrawingFix)

        val innerArcStart = innerArcPoint(startAngle)
        val innerArcEnd = innerArcPoint(endAngle - fullCircleDrawingFix)

        fun outerArcPoint(angle: Double) = arcPoint(radius, angle)
        fun innerArcPoint(angle: Double) = arcPoint(holeRadius, angle)

        private fun arcPoint(radius: Double, angle: Double): DoubleVector {
            return position.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
        }

        val sectorCenter: DoubleVector // center of the pie slice geometry
            get() {
                val offset = 0.5 * (radius - holeRadius)
                return position.add(DoubleVector(holeRadius * cos(direction), holeRadius * sin(direction)))
                    .add(DoubleVector(offset * cos(direction), offset * sin(direction)))
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
                        fillColor().set(getFillColor(p))
                        strokeColor().set(if (getFillColor(p) == Color.TRANSPARENT) Color.BLACK else strokeColor)
                        strokeWidth().set(1.5)
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
        pieCenter: DoubleVector,
        sectors: List<Sector>,
        ctx: GeomContext
    ) {
        if (ctx.annotations == null || sectors.isEmpty()) return

        // split sectors into left and right...
        val leftSectors = sectors
            .filter { it.outerArcStart.x < pieCenter.x || it.outerArcEnd.x < pieCenter.x || it.sectorCenter.x < pieCenter.x }
            .ifEmpty { sectors }
        val rightSectors = sectors
            .filter { it.outerArcStart.x > pieCenter.x || it.outerArcEnd.x > pieCenter.x || it.sectorCenter.x > pieCenter.x  }
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
        ).forEach { root.add(it) }
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
            Colors.luminance(getFillColor(sector.p)) < 0.5 -> Color.WHITE // if fill is dark
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

            return Side.values().flatMap { side -> createForSide(side) }
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

    override fun widthSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        if (!isDiscrete) return null
        return dimensionSpan(p, coordAes)
    }

    override fun heightSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        if (!isDiscrete) return null
        return dimensionSpan(p, coordAes)
    }
}
