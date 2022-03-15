/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.chart.Utils.changeAlphaWithMin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.fillRect
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.strokeRect
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorHelper
import jetbrains.livemap.searching.SearchResult
import kotlin.math.abs

object BarChart {

    private data class Column(
        val index: Int,
        val rect: Rect<Client>,
        val color: Color
    )

    private fun splitColumns(symbol: SymbolComponent, scaleFactor: Double): List<Column> {
        val chartDimension = symbol.size * scaleFactor
        val centerOffset = chartDimension.x / 2
        val columnWidth = chartDimension.x / symbol.values.size

        return symbol.values.indices.map {
            val columnHeight = chartDimension.y * abs(symbol.values[it])

            Column(
                index = symbol.indices[it],
                rect = Rect(
                    origin = Vec(
                        x = columnWidth * it - centerOffset,
                        y = if (symbol.values[it] > 0) -columnHeight else 0.0
                    ),
                    dimension = Vec(
                        x = columnWidth,
                        y = columnHeight
                    )
                ),
                color = symbol.colors[it]
            )
        }
    }


    class Renderer : jetbrains.livemap.mapengine.Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbol = entity.get<SymbolComponent>()

            splitColumns(symbol, chartElement.scalingSizeFactor).forEach { column ->
                ctx.setFillStyle(changeAlphaWithMin(column.color, chartElement.scalingAlphaValue))

                ctx.fillRect(column.rect)

                if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
                    ctx.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
                    ctx.setLineWidth(chartElement.strokeWidth)
                    ctx.strokeRect(column.rect)
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

            splitColumns(symbol, chartElement.scalingSizeFactor).forEach { column ->
                target.get<ScreenLoopComponent>().origins.forEach {
                    if(column.rect.contains(coord - it)) {
                        return SearchResult(
                            target.get<IndexComponent>().layerIndex,
                            index = column.index
                        )
                    }
                }
            }

            return null
        }

        override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity) = throw NotImplementedError()

        companion object {
            val LOCATABLE_COMPONENTS = listOf(SymbolComponent::class, ScreenLoopComponent::class)
        }
    }
}
