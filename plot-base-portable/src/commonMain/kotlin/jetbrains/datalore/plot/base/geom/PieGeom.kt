/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.collections.filterNotNullKeys
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.toRadians
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
            .mapValues { (pieCenter, dataPoints) -> buildSectors(pieCenter, dataPoints) }

        val svgPies = pies.flatMap { (center, sectors) -> buildSvgPie(center, sectors, ctx) }

        appendNodes(svgPies, root)
    }

    private fun buildSvgPie(
        pieCenter: DoubleVector, // todo: remove
        sectors: List<Sector>,
        ctx: GeomContext
    ): List<LinePath> {
        val result = ArrayList<LinePath>()
        sectors.forEach { sector ->
            val middleAngle = (sector.startAngle + sector.endAngle) / 2
            //val sectorCenter = shift(pieCenter, sector.explode, middleAngle)
            val sectorCenter = getCoordinate(pieCenter, middleAngle, sector.explode)

            val linePath = buildSvgSector(sectorCenter, sector)
            result.add(linePath)

            buildHint(sectorCenter, sector, getFillColor(sector.p), ctx)
        }
        return result
    }

    private fun shift(v: DoubleVector, l: Double, angle: Double): DoubleVector {
        return DoubleVector(0.0, l).rotate(angle).add(v)
    }

    private fun getCoordinate(center: DoubleVector, angle: Double, radius: Double): DoubleVector {
        return center
            .add(DoubleVector(0.0, -radius).rotate(angle)
        )
    }

    private fun buildSvgSector(
        location: DoubleVector, // todo: remove
        sector: Sector
    ): LinePath {

        // Fix full circle drawing
        var endAngle = sector.endAngle
        if ((sector.endAngle - sector.startAngle) % (2 * PI) == 0.0) {
            endAngle -= 0.0001
        }
        //val innerPnt1 = shift(sector.sectorCenter, sector.startAngle, sector.holeRadius)
        //val outerPnt1 = shift(sector.sectorCenter, sector.startAngle, sector.radius)
        //val outerPnt2 = shift(sector.sectorCenter, endAngle, sector.radius)
        //val innerPnt2 = shift(sector.sectorCenter, endAngle, sector.holeRadius)
        val innerPnt1 = getCoordinate(location, sector.startAngle, sector.holeRadius)
        val outerPnt1 = getCoordinate(location, sector.startAngle, sector.radius)
        val outerPnt2 = getCoordinate(location, endAngle, sector.radius)
        val innerPnt2 = getCoordinate(location, endAngle, sector.holeRadius)

        val largeArc = (sector.endAngle - sector.startAngle) > PI

        val builder = SvgPathDataBuilder().apply {
            moveTo(innerPnt1)
            lineTo(outerPnt1)
            ellipticalArc(
                rx = sector.radius,
                ry = sector.radius,
                xAxisRotation = 0.0,
                largeArc = largeArc,
                sweep = true,
                to = outerPnt2
            )
            lineTo(innerPnt2)
            ellipticalArc(
                rx = sector.holeRadius,
                ry = sector.holeRadius,
                xAxisRotation = 0.0,
                largeArc = largeArc,
                sweep = false,
                to = innerPnt1
            )
        }

        return LinePath(builder).apply {
            val fill = getFillColor(sector.p)
            val fillAlpha = AestheticsUtil.alpha(fill, sector.p)
            fill().set(Colors.withOpacity(fill, fillAlpha))
            width().set(strokeWidth)
            color().set(strokeColor)
        }
    }

    private fun buildHint(location: DoubleVector, sector: Sector, color: Color, ctx: GeomContext) {
        val step = toRadians(15.0)
        val middleAngles =
            generateSequence(sector.startAngle) { it + step }.takeWhile { it < sector.endAngle } + sector.endAngle
        val points = listOf(getCoordinate(location, sector.startAngle, sector.holeRadius)) +
                middleAngles.map { getCoordinate(location, angle = it, sector.radius) } +
                middleAngles.toList().reversed().map { getCoordinate(location, angle = it, sector.holeRadius) }

        ctx.targetCollector.addPolygon(
            points = points,
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
        val holeRadius = radius * holeSize
        val explode = radius * p.explode()!!
        val sectorCenter = shift(pieCenter, explode, (startAngle + endAngle) / 2)
    }

    private fun buildSectors(pieCenter: DoubleVector, dataPoints: List<DataPointAesthetics>): List<Sector> {
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
