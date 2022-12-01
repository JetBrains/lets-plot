/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.algorithms.AdaptiveResampler
import jetbrains.datalore.base.collections.filterNotNullKeys
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.DataPointAestheticsDelegate
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.TextUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.vis.TextStyle
import jetbrains.datalore.vis.svg.SvgCircleElement
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class PieGeom : GeomBase() {
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
        val sectors = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.SLICE)
            .groupBy { p -> geomHelper.toClient(p.x()!!, p.y()!!, p) }
            .filterNotNullKeys()
            .mapValues { (pieCenter, dataPoints) -> computeSectors(pieCenter, dataPoints) }
            .values
            .flatten()

        sectors.forEach { buildHint(it, ctx.targetCollector) }
        appendNodes(sectors.map(::buildSvgSector), root)

        if (ctx.annotations != null) {
            buildAnnotations(root, sectors, ctx)
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

        var currentAngle = -PI / 2.0
        currentAngle -= angle(dataPoints.first()) // not sure why

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
        val sectorCenter = pieCenter.add(DoubleVector(explode * cos(direction), explode * sin(direction)))
        private val fullCircleDrawingFix = if (angle % (2 * PI) == 0.0) 0.0001 else 0.0

        val outerArcStart = outerArcPoint(startAngle)
        val outerArcEnd = outerArcPoint(endAngle - fullCircleDrawingFix)

        val innerArcStart = innerArcPoint(startAngle)
        val innerArcEnd = innerArcPoint(endAngle - fullCircleDrawingFix)

        fun outerArcPoint(angle: Double) = arcPoint(radius, angle)
        fun innerArcPoint(angle: Double) = arcPoint(holeRadius, angle)

        private fun arcPoint(radius: Double, angle: Double): DoubleVector {
            return sectorCenter.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
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

    private fun buildAnnotations(
        root: SvgRoot,
        sectors: List<Sector>,
        ctx: GeomContext
    ) {
        fun toTextDataPointAesthetics(p: DataPointAesthetics, color: Color? = null): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                val textStyle: TextStyle = ctx.annotations!!.textStyle

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.SIZE -> textStyle.size
                        Aes.FAMILY -> textStyle.family
                        Aes.FONTFACE -> textStyle.face.toString()
                        Aes.COLOR -> color ?: p.color()
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
        val textSizeGetter: (String, DataPointAesthetics) -> DoubleVector =
            { text, p -> TextUtil.measure(text, toTextDataPointAesthetics(p), ctx) }

        val annotationGeom = TextGeom()

        sectors.forEach { sector ->
            val text = ctx.annotations!!.getAnnotationText(sector.p.index())
            val (location, isOutside) = placeAnnotation(sector, textSizeGetter(text, sector.p))

            // choose color for text
            val textColor = when {
                isOutside -> ctx.annotations!!.textStyle.color // or use sector's color?
                Colors.luminance(getFillColor(sector.p)) < 0.5 -> Color.WHITE // if fill is dark
                else -> Color.BLACK
            }

            val g = annotationGeom.buildTextComponent(
                toTextDataPointAesthetics(sector.p, textColor),
                location,
                text,
                sizeUnitRatio = 1.0,
                ctx,
                boundsCenter = null
            )
            root.add(g)
        }
    }

    private fun placeAnnotation(
        sector: Sector,
        textSize: DoubleVector
    ): Pair<DoubleVector, Boolean> {
        val placeOutside: Boolean
        val location = run {
            // center of the pie slice geometry:
            val centralPos = with(sector) {
                val offset = 0.5 * (sector.radius - sector.holeRadius)
                sectorCenter.add(DoubleVector(holeRadius * cos(direction), holeRadius * sin(direction)))
                    .add(DoubleVector(offset * cos(direction), offset * sin(direction)))
            }

            val textRect = DoubleRectangle(centralPos.subtract(textSize.mul(0.5)), textSize)
            val side1 = DoubleSegment(sector.innerArcStart, sector.outerArcStart)
            val side2 = DoubleSegment(sector.innerArcEnd, sector.outerArcEnd)
            placeOutside = textRect.parts.any { side1.intersection(it) != null || side2.intersection(it) != null }

            if (placeOutside) {
                val offset = 1.25 * sector.radius
                with(sector) {
                    sectorCenter.add(DoubleVector(offset * cos(direction), offset * sin(direction)))
                }
            } else {
                centralPos
            }
        }
        return location to placeOutside
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
