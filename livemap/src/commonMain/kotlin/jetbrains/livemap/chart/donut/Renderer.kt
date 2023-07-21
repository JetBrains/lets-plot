/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.donut

import org.jetbrains.letsPlot.core.canvas.Context2d
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
            if (sector.fillColor == null) {
                return
            }

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

        fun arcs(sector: Sector) {
             if (sector.strokeColor == null || sector.strokeWidth == 0.0) {
                 return
             }

            ctx.apply {
                setStrokeStyle(sector.strokeColor)
                setLineWidth(sector.strokeWidth)

                if (sector.drawInnerArc) {
                    beginPath()
                    arc(
                        sector.sectorCenter.x, sector.sectorCenter.y,
                        radius = max(0.0, sector.holeRadius),
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    stroke()
                }

                if (sector.drawOuterArc) {
                    beginPath()
                    arc(
                        sector.sectorCenter.x, sector.sectorCenter.y,
                        radius = sector.radius,
                        startAngle = sector.startAngle,
                        endAngle = sector.endAngle
                    )
                    stroke()
                }
            }
        }

        fun spacers(sector: Sector) {
            if (sector.spacerColor == null || sector.spacerWidth == 0.0) {
                return
            }
            ctx.apply {
                setStrokeStyle(sector.spacerColor)
                setLineWidth(sector.spacerWidth)

                if (sector.drawSpacerAtStart) {
                    beginPath()
                    moveTo(sector.innerArcStart.x, sector.innerArcStart.y)
                    lineTo(sector.outerArcStart.x, sector.outerArcStart.y)
                    stroke()
                }
                if (sector.drawSpacerAtEnd) {
                    beginPath()
                    moveTo(sector.innerArcEnd.x, sector.innerArcEnd.y)
                    lineTo(sector.outerArcEnd.x, sector.outerArcEnd.y)
                    stroke()
                }
            }
        }

        computeSectors(pieSpec, chartElement.scalingSizeFactor).forEach { sector ->
            fillSector(sector)
            arcs(sector)
            spacers(sector)
        }
    }
}