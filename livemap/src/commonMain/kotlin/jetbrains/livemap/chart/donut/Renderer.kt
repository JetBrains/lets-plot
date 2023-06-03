/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.donut

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PieSpecComponent
import jetbrains.livemap.chart.changeAlphaWithMin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.translate
import kotlin.math.max

class Renderer : jetbrains.livemap.mapengine.Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val chartElement = entity.get<ChartElementComponent>()
        val pieSpec = entity.get<PieSpecComponent>()

        ctx.translate(renderHelper.dimToScreen(entity.get<WorldOriginComponent>().origin))

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