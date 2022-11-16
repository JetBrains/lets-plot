/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.algorithms.AdaptiveResampler
import jetbrains.datalore.base.collections.filterNotNullKeys
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
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

        val pies = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.SLICE)
            .groupBy { p -> geomHelper.toClient(p.x()!!, p.y()!!, p) }
            .filterNotNullKeys()
            .mapValues { (pieCenter, dataPoints) -> computeSectors(pieCenter, dataPoints) }
            .values

        pies.forEach { sectors -> sectors.forEach { buildHint(it, getFillColor(it.p), ctx.targetCollector) } }

        val svgPies = pies.flatMap { sectors -> sectors.map(::buildSvgSector) }
        appendNodes(svgPies, root)
    }

    private fun buildSvgSector(sector: Sector): LinePath {
        // Fix full circle drawing
        var endAngle = sector.endAngle
        if ((sector.endAngle - sector.startAngle) % (2 * PI) == 0.0) {
            endAngle -= 0.0001
        }

        val largeArc = (sector.endAngle - sector.startAngle) > PI

        return LinePath(
            SvgPathDataBuilder().apply {
                moveTo(sector.innerStart)
                lineTo(sector.outerStart)
                ellipticalArc(
                    rx = sector.radius,
                    ry = sector.radius,
                    xAxisRotation = 0.0,
                    largeArc = largeArc,
                    sweep = true,
                    to = sector.outerEnd
                )
                lineTo(sector.innerEnd)
                ellipticalArc(
                    rx = sector.holeRadius,
                    ry = sector.holeRadius,
                    xAxisRotation = 0.0,
                    largeArc = largeArc,
                    sweep = false,
                    to = sector.innerStart
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

    private fun buildHint(sector: Sector, color: Color, targetCollector: GeomTargetCollector) {
        fun arcResampler(sector: Sector, innerArc: Boolean): (DoubleVector) -> DoubleVector {
            val transform = when(innerArc) {
                true -> sector::innerArcPoint
                false -> sector::outerArcPoint
            }

            val segmentLength = when(innerArc) {
                true -> sector.innerStart.subtract(sector.innerEnd).length()
                false -> sector.outerStart.subtract(sector.outerEnd).length()
            }

            val startPoint = when(innerArc) {
                true -> sector.innerStart
                false -> sector.outerStart
            }

            return { p: DoubleVector ->
                val ratio = p.subtract(startPoint).length() / segmentLength
                if (ratio.isFinite()) {
                    val angle = sector.startAngle + sector.angle * ratio
                    transform(angle)
                } else {
                    p
                }
            }
        }

        val outerArc = AdaptiveResampler.forDoubleVector(arcResampler(sector, innerArc = false), 2.0).resample(sector.outerStart, sector.outerEnd)
        val innerArc = AdaptiveResampler.forDoubleVector(arcResampler(sector, innerArc = true), 2.0).resample(sector.innerStart, sector.innerEnd)

        targetCollector.addPolygon(
            points = outerArc + innerArc.reversed(),
            localToGlobalIndex = { sector.p.index() },
            GeomTargetCollector.TooltipParams(markerColors = listOf(color))
        )
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

    private inner class Sector(
        val pieCenter: DoubleVector,
        val p: DataPointAesthetics,
        val startAngle: Double,
        val endAngle: Double
    ) {
        val radius: Double = AesScaling.pieDiameter(p) / 2
        private val direction = (endAngle - startAngle) / 2
        private val explode = radius * p.explode()!!
        private val sectorCenter = DoubleVector(0.0, explode).rotate(direction).add(pieCenter)
        val holeRadius = radius * holeSize
        val angle = abs(endAngle - startAngle)

        val outerStart = outerArcPoint(startAngle)
        val outerEnd = outerArcPoint(endAngle)

        val innerStart = innerArcPoint(startAngle)
        val innerEnd = innerArcPoint(endAngle)

        fun outerArcPoint(angle: Double) = arcPoint(radius, angle)
        fun innerArcPoint(angle: Double) = arcPoint(holeRadius, angle)

        private fun arcPoint(radius: Double, angle: Double): DoubleVector {
            return sectorCenter.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
        }
    }

    private fun computeSectors(pieCenter: DoubleVector, dataPoints: List<DataPointAesthetics>): List<Sector> {
        val values = dataPoints.map { it.slice()!! }
        var currentAngle = Double.NaN
        return transformValues2Angles(values).withIndex()
            .map { (index, angle) ->
                if (currentAngle.isNaN()) {
                    currentAngle = -angle
                }
                Sector(
                    p = dataPoints[index],
                    pieCenter = pieCenter,
                    startAngle = currentAngle,
                    endAngle = currentAngle + angle
                ).also { currentAngle = it.endAngle }
            }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun transformValues2Angles(values: List<Double>): List<Double> {
            val sum = values.sumOf(::abs)
            return if (sum == 0.0) {
                MutableList(values.size) { 2 * PI / values.size }
            } else {
                values.map { 2 * PI * abs(it) / sum }
            }
        }
    }

    private fun getFillColor(p: DataPointAesthetics): Color {
        return when (fillWithColor) {
            true -> p.color()!!
            false -> p.fill()!!
        }
    }
}
