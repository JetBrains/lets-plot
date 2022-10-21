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
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgRectElement
import kotlin.math.PI
import kotlin.math.abs


class PieGeom : GeomBase() {

    // ToDo: Add space between pieces and color of these gaps (TRANSPARENT as default)

    var holeRatio: Double = 0.0

    private var myFillColorMapper: (DataPointAesthetics) -> Color = fillColorMapper(Aes.FILL)

    fun setAesForFill(aes: Aes<*>) {
        myFillColorMapper = fillColorMapper(aes)
    }

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PieLegendKeyElementFactory(myFillColorMapper, strokeColor = Color.TRANSPARENT)

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
            val linePath = buildSector(location, sector, dataPoints[index])
            result.add(linePath)

            buildHint(location, sector, ctx)
        }
        return result
    }

    private fun buildSector(
        location: DoubleVector,
        sector: Sector,
        p: DataPointAesthetics
    ): LinePath {
        val geometry = if (holeRatio == 0.0) ::buildPieSector else ::buildDonutSector
        val linePath = geometry(location, sector) // ToDo: Use unified method
        val fill = myFillColorMapper(p)
        val fillAlpha = AestheticsUtil.alpha(fill, p)
        linePath.fill().set(Colors.withOpacity(fill, fillAlpha))
        linePath.width().set(1.0)
        linePath.color().set(Color.WHITE)
        return linePath
    }

    private fun buildPieSector(location: DoubleVector, sector: Sector): LinePath {
        val basis = DoubleVector(0.0, -sector.radius)
        val builder = SvgPathDataBuilder()
        builder.moveTo(location)
        builder.lineTo(location.add(basis.rotate(sector.startAngle)))
        val arcTo = location.add(basis.rotate(sector.endAngle))
        builder.ellipticalArc(
            rx = sector.radius,
            ry = sector.radius,
            xAxisRotation = 0.0,
            largeArc = (sector.endAngle - sector.startAngle) > PI,
            sweep = true,
            to = arcTo
        )
        builder.closePath()
        return LinePath(builder)
    }

    private fun buildDonutSector(location: DoubleVector, sector: Sector): LinePath {
        val builder = SvgPathDataBuilder()

        val innerRadius = sector.radius * holeRatio
        val largeArc = (sector.endAngle - sector.startAngle) > PI

        val startPoint = location.add(DoubleVector(0.0, -innerRadius).rotate(sector.startAngle))
        builder.moveTo(startPoint)
        builder.lineTo(location.add(DoubleVector(0.0, -sector.radius).rotate(sector.startAngle)))
        builder.ellipticalArc(
            rx = sector.radius,
            ry = sector.radius,
            xAxisRotation = 0.0,
            largeArc = largeArc,
            sweep = true,
            to = location.add(DoubleVector(0.0, -sector.radius).rotate(sector.endAngle))
        )
        builder.lineTo(location.add(DoubleVector(0.0, -innerRadius).rotate(sector.endAngle)))
        builder.ellipticalArc(
            rx = innerRadius,
            ry = innerRadius,
            xAxisRotation = 0.0,
            largeArc = largeArc,
            sweep = false,
            to = startPoint
        )
        return LinePath(builder)
    }

    private fun buildHint(location: DoubleVector, sector: Sector, ctx: GeomContext) {
        val innerRadius = sector.radius * holeRatio

        val step = toRadians(15.0)
        val middleAngles =
            generateSequence(sector.startAngle) { it + step }.takeWhile { it < sector.endAngle } + sector.endAngle
        val points = listOf(location.add(DoubleVector(0.0, -innerRadius).rotate(sector.startAngle))) +
                middleAngles.map { location.add(DoubleVector(0.0, -sector.radius).rotate(it)) } +
                location.add(DoubleVector(0.0, -innerRadius).rotate(sector.endAngle)) +
                middleAngles.toList().reversed().map { location.add(DoubleVector(0.0, -innerRadius).rotate(it)) }

        ctx.targetCollector.addPolygon(
            points = points,
            localToGlobalIndex = { sector.dataPointIndex },
            GeomTargetCollector.TooltipParams()
        )
    }

    private class PieLegendKeyElementFactory(
        private val fillColorMapper: (DataPointAesthetics) -> Color,
        private val strokeColor: Color
    ) : LegendKeyElementFactory {
        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val rect = SvgRectElement(0.0, 0.0, size.x, size.y).apply {
                fillColor().set(fillColorMapper(p))
                fillOpacity().set(p.alpha())
                strokeColor().set(strokeColor)
                strokeWidth().set(1.5) // set thickness
            }
            val g = SvgGElement()
            g.children().add(rect)
            return g
        }
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
                .sortedByDescending { it.value } // ToDo: remove it!
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