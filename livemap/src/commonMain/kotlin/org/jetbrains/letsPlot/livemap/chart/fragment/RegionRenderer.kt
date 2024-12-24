/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
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
        val fragmentsFaces: ArrayList<Polygon<World>> = ArrayList()

        for (fragment in fragments) {
            val fragmentComponent = fragment.get<FragmentComponent>()
            val geometry = fragment.tryGet<WorldGeometryComponent>()?.geometry ?: error("")

            regionBorder = MultiLineString(regionBorder.plus(fragmentComponent.boundary))
            fragmentsFaces.addAll(geometry.multiPolygon)
        }

        val regionFace = MultiPolygon(fragmentsFaces)

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)

        ctx.beginPath()
        ctx.drawMultiPolygon(regionFace) { nop() }
        ctx.closePath()

        if (chartElement.fillColor != null) {
            ctx.setFillStyle(chartElement.scaledFillColor())
            ctx.fill()
        }
        ctx.restore()
        if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
            ctx.save()
            ctx.scale(renderHelper.zoomFactor)

            ctx.beginPath()
            ctx.drawMultiLineString(regionBorder) { nop() }

            ctx.restore()
            ctx.save()

            ctx.setStrokeStyle(chartElement.scaledStrokeColor())
            ctx.setLineDash(chartElement.scaledLineDash())
            ctx.setLineDashOffset(chartElement.scaledLineDashOffset())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.setLineCap(LineCap.ROUND)
            ctx.setLineJoin(LineJoin.ROUND)
            ctx.stroke()

            ctx.restore()
        }
    }

    private fun nop() {}
}