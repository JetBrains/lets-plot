/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

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
import kotlin.math.floor
import kotlin.math.max

object DonutChart {

    private data class Sector(
        val index: Int,
        val radius: Double,
        val startAngle: Double,
        val endAngle: Double,
        val color: Color,
    )

    private fun splitSectors(symbol: SymbolComponent, scaleFactor: Double): List<Sector> {
        var currentAngle = -PI / 2
        return symbol.values.indices.map {
            val endAngle = currentAngle + symbol.values[it]
            Sector(
                index = symbol.indices[it],
                radius = symbol.size.x * scaleFactor / 2.0,
                startAngle = currentAngle,
                endAngle = endAngle,
                color = symbol.colors[it]
            ).also { currentAngle = endAngle }
        }
    }

    class Renderer : jetbrains.livemap.mapengine.Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbol = entity.get<SymbolComponent>()

            splitSectors(symbol, chartElement.scalingSizeFactor).forEach { sector ->
                val holeRadius = floor(sector.radius * 0.55)
                if (chartElement.strokeColor != null && chartElement.strokeWidth > 0.0) {

                    ctx.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
                    ctx.setLineWidth(chartElement.strokeWidth)

                    // draw inner arc
                    ctx.beginPath()
                    ctx.arc(
                        x = 0.0, y = 0.0,
                        radius = max(0.0, holeRadius - chartElement.strokeWidth / 2),
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    ctx.stroke()

                    // draw outer arc
                    ctx.beginPath()
                    ctx.arc(
                        x = 0.0, y = 0.0,
                        radius = sector.radius + chartElement.strokeWidth / 2,
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    ctx.stroke()
                }
                // fill sector
                ctx.setFillStyle(changeAlphaWithMin(sector.color, chartElement.scalingAlphaValue))
                ctx.beginPath()
                ctx.arc(0.0, 0.0, holeRadius, sector.startAngle, sector.endAngle)
                ctx.arc(0.0, 0.0, sector.radius, sector.endAngle, sector.startAngle, anticlockwise = true)
                ctx.fill()
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

            splitSectors(symbol, chartElement.scalingSizeFactor).forEach { (index, radius, startAngle, endAngle, color) ->
                    target.get<ScreenLoopComponent>().origins.forEach { origin ->
                        if (isCoordinateInPieSector(coord, origin, radius, startAngle, endAngle)) {
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
            if (angle < - PI / 2) {
                angle += 2 * PI
            }

            return startAngle <= angle && angle < endAngle
        }

        companion object {
            val LOCATABLE_COMPONENTS = listOf(SymbolComponent::class, ScreenLoopComponent::class)
        }
    }
}
