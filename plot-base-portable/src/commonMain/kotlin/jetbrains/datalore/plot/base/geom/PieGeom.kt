/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

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

    var holeRatio: Double = 0.0
    var strokeWidth: Double = 0.0
    var strokeColor: Color = Color.WHITE

    private var myFillColorMapper: (DataPointAesthetics) -> Color = fillColorMapper(Aes.FILL)

    fun setAesForFill(aes: Aes<*>) {
        myFillColorMapper = fillColorMapper(aes)
    }

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PieLegendKeyElementFactory(myFillColorMapper, strokeColor)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.SLICE)
            .groupBy { p ->
                val point = DoubleVector(p.x()!!, p.y()!!)
                geomHelper.toClient(point, p)
            }
            .forEach { (location, dataPoints) ->
                if (location == null) return@forEach

                val sectors = buildPie(location, dataPoints, ctx)
                appendNodes(sectors, root)
            }
    }

    private fun buildPie(
        location: DoubleVector,
        dataPoints: List<DataPointAesthetics>,
        ctx: GeomContext
    ): List<LinePath> {
        val result = ArrayList<LinePath>()
        splitSectors(dataPoints).forEachIndexed { index, sector ->
            val sectorOffset = sector.radius * dataPoints[index].explode()!!
            val middleAngle = (sector.startAngle + sector.endAngle) / 2
            val sectorLocation = getCoordinate(location, middleAngle, sectorOffset)

            val linePath = buildSector(sectorLocation, sector, dataPoints[index])
            result.add(linePath)

            val colorMarkerMapper = { p: DataPointAesthetics -> listOf(myFillColorMapper(p)) }
            buildHint(sectorLocation, sector, colorMarkerMapper(dataPoints[index]), ctx)
        }
        return result
    }

    private fun getCoordinate(center: DoubleVector, angle: Double, radius: Double): DoubleVector {
        return center.add(
            DoubleVector(0.0, -radius).rotate(angle)
        )
    }

    private fun buildSector(
        location: DoubleVector,
        sector: Sector,
        p: DataPointAesthetics
    ): LinePath {
        val builder = SvgPathDataBuilder()

        val innerRadius = sector.radius * holeRatio

        // Fix full circle drawing
        var endAngle = sector.endAngle
        if ((sector.endAngle - sector.startAngle) % (2*PI) == 0.0) {
            endAngle -= 0.0001
        }
        val innerPnt1 = getCoordinate(location, sector.startAngle, innerRadius)
        val outerPnt1 = getCoordinate(location, sector.startAngle, sector.radius)
        val outerPnt2 = getCoordinate(location, endAngle, sector.radius)
        val innerPnt2 = getCoordinate(location, endAngle, innerRadius)

        val largeArc = (sector.endAngle - sector.startAngle) > PI

        builder.moveTo(innerPnt1)
        builder.lineTo(outerPnt1)
        builder.ellipticalArc(
            rx = sector.radius,
            ry = sector.radius,
            xAxisRotation = 0.0,
            largeArc = largeArc,
            sweep = true,
            to = outerPnt2
        )
        builder.lineTo(innerPnt2)
        builder.ellipticalArc(
            rx = innerRadius,
            ry = innerRadius,
            xAxisRotation = 0.0,
            largeArc = largeArc,
            sweep = false,
            to = innerPnt1
        )

        return LinePath(builder).apply {
            val fill = myFillColorMapper(p)
            val fillAlpha = AestheticsUtil.alpha(fill, p)
            fill().set(Colors.withOpacity(fill, fillAlpha))
            width().set(strokeWidth)
            color().set(strokeColor)
        }
    }

    private fun buildHint(location: DoubleVector, sector: Sector, markerColors: List<Color>, ctx: GeomContext) {
        val innerRadius = sector.radius * holeRatio

        val step = toRadians(15.0)
        val middleAngles =
            generateSequence(sector.startAngle) { it + step }.takeWhile { it < sector.endAngle } + sector.endAngle
        val points = listOf(getCoordinate(location, sector.startAngle, innerRadius)) +
                middleAngles.map { getCoordinate(location, angle = it, sector.radius) } +
                middleAngles.toList().reversed().map { getCoordinate(location, angle = it, innerRadius) }

        ctx.targetCollector.addPolygon(
            points = points,
            localToGlobalIndex = { sector.dataPointIndex },
            GeomTargetCollector.TooltipParams(markerColors = markerColors)
        )
    }

    private class PieLegendKeyElementFactory(
        private val fillColorMapper: (DataPointAesthetics) -> Color,
        private val strokeColor: Color
    ) : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val location = DoubleVector(size.x / 2, size.y / 2)
            val shapeSize = shapeSize(p)
            val rect = SvgCircleElement(location.x, location.y, shapeSize / 2).apply {
                fillColor().set(fillColorMapper(p))
                strokeColor().set(strokeColor)
                strokeWidth().set(1.0)
            }
            val g = SvgGElement()
            g.children().add(rect)
            return g
        }

        override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
            val shapeSize = shapeSize(p)
            val size = shapeSize + 4.0
            return DoubleVector(size, size)
        }

        private fun shapeSize(p: DataPointAesthetics) = p.size()!! * 2.5
    }

    companion object {
        const val HANDLES_GROUPS = false

        private data class Sector(
            val dataPointIndex: Int,
            val radius: Double,
            val startAngle: Double,
            val endAngle: Double
        )

        private fun splitSectors(dataPoints: List<DataPointAesthetics>): List<Sector> {
            val values = dataPoints.map { it.slice()!! }
            var currentAngle = Double.NaN
            return transformValues2Angles(values).withIndex()
                .map { (index, angle) ->
                    if (currentAngle.isNaN()) {
                        currentAngle = -angle
                    }
                    val endAngle = currentAngle + angle
                    Sector(
                        dataPointIndex = dataPoints[index].index(),
                        radius = AesScaling.pieDiameter(dataPoints[index]) / 2,
                        startAngle = currentAngle,
                        endAngle = endAngle
                    ).also { currentAngle = endAngle }
                }
        }

        private fun transformValues2Angles(values: List<Double>): List<Double> {
            val sum = values.sumOf(::abs)
            return if (sum == 0.0) {
                MutableList(values.size) { 2 * PI / values.size }
            } else {
                values.map { 2 * PI * abs(it) / sum }
            }
        }
    }

    private fun fillColorMapper(aes: Aes<*>): (DataPointAesthetics) -> Color {
        require(Aes.isColor(aes))
        return { p: DataPointAesthetics ->
            when (aes) {
                Aes.COLOR -> p.color()!!
                Aes.FILL -> p.fill()!!
                else -> error("Pie is not applicable to $aes aesthetic as fill color.")
            }
        }
    }
}