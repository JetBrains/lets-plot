/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.*

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {

        val fragments = entity.get<RegionFragmentsComponent>().fragments
        if (fragments.isEmpty()) {
            return
        }

        for (fragment in fragments) {
            if (fragment.tryGet<WorldGeometryComponent>() == null) {
                return
            }
        }

        val chartElement = entity.get<ChartElementComponent>()

        var regionBorder = MultiLineString<World>(emptyList())

        for (fragment in fragments) {
            val fragmentComponent = fragment.get<FragmentComponent>()
            val clipPath = fragmentComponent.clipPath
            val geometry = fragment.tryGet<WorldGeometryComponent>()?.geometry ?: error("")

            regionBorder = MultiLineString(regionBorder.plus(fragmentComponent.boundary))

            ctx.save()
            ctx.scale(renderHelper.zoomFactor)

            ctx.beginPath()
            ctx.drawMultiPolygon(clipPath) { nop() }
            ctx.closePath()
            ctx.restore()
            ctx.save()
            ctx.clip()
            ctx.scale(renderHelper.zoomFactor)

            ctx.beginPath()
            ctx.drawMultiPolygon(geometry.multiPolygon) { nop() }
            ctx.closePath()

            ctx.setFillStyle(chartElement.scaledFillColor())
            ctx.fill()
            ctx.restore()
        }

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)

        ctx.beginPath()
        ctx.drawMultiLineString(regionBorder) { nop() }

        ctx.restore()
        ctx.save()

        ctx.setStrokeStyle(chartElement.scaledStrokeColor())
        ctx.setLineWidth(chartElement.scaledStrokeWidth())
        ctx.setLineCap(LineCap.ROUND)
        ctx.setLineJoin(LineJoin.ROUND)
        ctx.stroke()
        ctx.restore()
    }

    private fun nop() {}
}