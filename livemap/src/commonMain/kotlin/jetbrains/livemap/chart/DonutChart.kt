/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Vec
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

object DonutChart {

    private data class Sector(
        val index: Int,
        val radius: Double,
        val startAngle: Double,
        val endAngle: Double,
        val color: Color,
        val explode: Double
    )

    private fun splitSectors(symbol: SymbolComponent, scaleFactor: Double): List<Sector> {
        var currentAngle = Double.NaN
        return symbol.values.indices.map { index ->
                val angle = symbol.values[index]
                if (currentAngle.isNaN()) {
                    currentAngle = -angle - PI/2
                }
                val endAngle = currentAngle + angle
                val radius = symbol.size.x * scaleFactor / 2.0
                Sector(
                    index = symbol.indices[index],
                    radius = radius,
                    startAngle = currentAngle,
                    endAngle = endAngle,
                    color = symbol.colors[index],
                    explode = radius * symbol.explodeValues[index]
                ).also { currentAngle = endAngle }
            }
    }

    class Renderer : jetbrains.livemap.mapengine.Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbol = entity.get<SymbolComponent>()
            val holeRatio = entity.get<PieSpecComponent>().holeRatio

            splitSectors(symbol, chartElement.scalingSizeFactor).forEach { sector ->
                val sectorOffset = sector.explode
                val middleAngle = (sector.startAngle + sector.endAngle) / 2
                val location = DoubleVector.ZERO.add(DoubleVector(0.0, -sectorOffset).rotate(middleAngle + PI / 2))

                val holeRadius = floor(sector.radius * holeRatio)

                // fill sector
                ctx.setFillStyle(changeAlphaWithMin(sector.color, chartElement.scalingAlphaValue))
                ctx.beginPath()
                ctx.arc(location.x, location.y, holeRadius, sector.startAngle, sector.endAngle)
                ctx.arc(location.x, location.y, sector.radius, sector.endAngle, sector.startAngle, anticlockwise = true)
                ctx.fill()

                // stroke
                if (chartElement.strokeColor != null && chartElement.strokeWidth > 0.0) {

                    ctx.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
                    ctx.setLineWidth(chartElement.strokeWidth)

                    // draw inner arc
                    ctx.beginPath()
                    ctx.arc(
                        location.x, location.y,
                        radius = max(0.0, holeRadius),
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    ctx.stroke()

                    // draw outer arc
                    ctx.beginPath()
                    ctx.arc(
                        location.x, location.y,
                        radius = sector.radius,
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    ctx.stroke()

                    // sides
                    fun side(angle: Double) {
                        ctx.beginPath()
                        val p1 = location.add(DoubleVector(0.0, -holeRadius).rotate(angle + PI / 2))
                        val p2 = location.add(DoubleVector(0.0, -sector.radius).rotate(angle + PI / 2))
                        ctx.moveTo(p1.x, p1.y)
                        ctx.lineTo(p2.x, p2.y)
                        ctx.stroke()
                    }
                    side(sector.startAngle)
                    side(sector.endAngle)
                }
            }
        }
    }


    class Locator : LocatorHelper {
        override fun search(coord: Vec<Client>, target: EcsEntity): SearchResult? {
            if (!target.contains(LOCATABLE_COMPONENTS)) {
                return null
            }

            val chartElement = target.get<ChartElementComponent>()
            val symbol = target.get<SymbolComponent>()

            splitSectors(symbol, chartElement.scalingSizeFactor).forEach { (index, radius, startAngle, endAngle, _, explode) ->
                    target.get<ScreenLoopComponent>().origins.forEach { origin ->
                        val middleAngle = (startAngle + endAngle) / 2
                        val offsetBasis = DoubleVector(0.0, -explode).rotate(middleAngle + PI / 2)
                        val loc = Vec<Client>(origin.x + offsetBasis.x, origin.y + offsetBasis.y)
                        if (isCoordinateInPieSector(coord, loc, radius, startAngle, endAngle)) {
                            return SearchResult(
                                layerIndex = target.get<IndexComponent>().layerIndex,
                                index = index
                            )
                        }
                    }
                }

            return null
        }

        override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity) = throw NotImplementedError()

        private fun isCoordinateInPieSector(coord: Vec<Client>, origin: Vec<Client>, radius: Double, startAngle: Double, endAngle: Double): Boolean {
            if (LocatorUtil.distance(coord, origin) > radius) {
                return false
            }

            var angle = LocatorUtil.calculateAngle(origin, coord)
            if (angle in -PI/2..PI && abs(startAngle) > PI) {
                angle -= 2 * PI
            }
            return startAngle <= angle && angle < endAngle
        }

        companion object {
            val LOCATABLE_COMPONENTS = listOf(SymbolComponent::class, ScreenLoopComponent::class)
        }
    }
}
