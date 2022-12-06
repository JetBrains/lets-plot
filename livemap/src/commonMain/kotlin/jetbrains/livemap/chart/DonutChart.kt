/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.toDoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.chart.Utils.changeAlphaWithMin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorHelper
import jetbrains.livemap.searching.LocatorUtil
import jetbrains.livemap.searching.SearchResult
import jetbrains.livemap.toClientPoint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

object DonutChart {

    private class Sector(
        val index: Int,
        val radius: Double,
        val holeRadius: Double,
        val fillColor: Color,
        val startAngle: Double,
        val endAngle: Double,
        explode: Double
    ) {
        private val angle = endAngle - startAngle
        private val direction = startAngle + angle / 2

        val sectorCenter = DoubleVector(explode * cos(direction), explode * sin(direction))

        val outerArcStart = outerArcPoint(startAngle)
        val outerArcEnd = outerArcPoint(endAngle)

        val innerArcStart = innerArcPoint(startAngle)
        val innerArcEnd = innerArcPoint(endAngle)

        fun outerArcPoint(angle: Double) = arcPoint(radius, angle)
        fun innerArcPoint(angle: Double) = arcPoint(holeRadius, angle)

        private fun arcPoint(radius: Double, angle: Double): DoubleVector {
            return sectorCenter.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
        }
    }

    private fun computeSectors(pieSpec: PieSpecComponent, scaleFactor: Double): List<Sector> {
        val sum = pieSpec.sliceValues.sum()
        fun angle(slice: Double) = when (sum) {
            0.0 -> 1.0 / pieSpec.sliceValues.size
            else -> abs(slice) / sum
        }.let { PI * 2.0 * it }

        // the first slice goes to the left of 12 o'clock and others go clockwise
        var currentAngle = -PI / 2.0
        currentAngle -= angle(pieSpec.sliceValues.first())

        val radius = pieSpec.radius * scaleFactor
        return pieSpec.sliceValues.indices.map { index ->
            Sector(
                index = pieSpec.indices[index],
                radius = radius,
                holeRadius = radius * pieSpec.holeSize,
                fillColor = pieSpec.colors[index],
                startAngle = currentAngle,
                endAngle = currentAngle + angle(pieSpec.sliceValues[index]),
                explode = pieSpec.explodeValues?.get(index)?.let { radius * it } ?: 0.0,
            ).also { sector -> currentAngle = sector.endAngle }
        }
    }

    class Renderer : jetbrains.livemap.mapengine.Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val pieSpec = entity.get<PieSpecComponent>()

            fun fillSector(sector: Sector) {
                ctx.setFillStyle(changeAlphaWithMin(sector.fillColor, chartElement.scalingAlphaValue))
                ctx.beginPath()
                with(sector) {
                    ctx.arc(
                        sectorCenter.x,
                        sectorCenter.y,
                        holeRadius,
                        startAngle,
                        endAngle
                    )
                    ctx.arc(
                        sectorCenter.x,
                        sectorCenter.y,
                        radius,
                        endAngle,
                        startAngle,
                        anticlockwise = true
                    )
                }
                ctx.fill()
            }

            fun strokeSector(sector: Sector) {
                if (chartElement.strokeColor == null || chartElement.strokeWidth == 0.0) {
                    return
                }
                ctx.apply {
                    setStrokeStyle(
                        changeAlphaWithMin(
                            chartElement.strokeColor!!,
                            chartElement.scalingAlphaValue
                        )
                    )
                    setLineWidth(chartElement.strokeWidth)

                    // draw inner arc
                    beginPath()
                    arc(
                        sector.sectorCenter.x, sector.sectorCenter.y,
                        radius = max(0.0, sector.holeRadius),
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    stroke()

                    // draw outer arc
                    beginPath()
                    arc(
                        sector.sectorCenter.x, sector.sectorCenter.y,
                        radius = sector.radius,
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    stroke()

                    // sides
                    beginPath()
                    moveTo(sector.innerArcStart.x, sector.innerArcStart.y)
                    lineTo(sector.outerArcStart.x, sector.outerArcStart.y)
                    stroke()

                    beginPath()
                    moveTo(sector.innerArcEnd.x, sector.innerArcEnd.y)
                    lineTo(sector.outerArcEnd.x, sector.outerArcEnd.y)
                    stroke()

                }
            }

            computeSectors(pieSpec, chartElement.scalingSizeFactor).forEach { sector ->
                fillSector(sector)
                strokeSector(sector)
            }
        }
    }


    class Locator : LocatorHelper {
        override fun search(coord: Vec<Client>, target: EcsEntity): SearchResult? {
            if (!target.contains(LOCATABLE_COMPONENTS)) {
                return null
            }

            val chartElement = target.get<ChartElementComponent>()
            val pieSpec = target.get<PieSpecComponent>()

            computeSectors(pieSpec, chartElement.scalingSizeFactor).forEach { sector ->
                target.get<ScreenLoopComponent>().origins.forEach { origin ->
                    val loc = origin.toDoubleVector().add(sector.sectorCenter)
                    if (isCoordinateInPieSector(coord, loc.toClientPoint(), sector.holeRadius, sector.radius, sector.startAngle, sector.endAngle)) {
                        return SearchResult(
                            layerIndex = target.get<IndexComponent>().layerIndex,
                            index = sector.index
                        )
                    }
                }
            }

            return null
        }

        override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity) = throw NotImplementedError()

        private fun isCoordinateInPieSector(
            coord: Vec<Client>,
            origin: Vec<Client>,
            holeRadius: Double,
            radius: Double,
            startAngle: Double,
            endAngle: Double
        ): Boolean {
            if (LocatorUtil.distance(coord, origin) !in holeRadius..radius) {
                return false
            }

            val angle = LocatorUtil.calculateAngle(origin, coord).let {
                when {
                    it in -PI / 2..PI && abs(startAngle) > PI -> it - 2 * PI
                    it in -PI..-PI / 2 && abs(endAngle) > PI -> it + 2 * PI
                    else -> it
                }
            }
            return startAngle <= angle && angle < endAngle
        }

        companion object {
            val LOCATABLE_COMPONENTS = listOf(PieSpecComponent::class, ScreenLoopComponent::class)
        }
    }
}
