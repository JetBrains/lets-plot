/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.donut

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import jetbrains.livemap.Client
import jetbrains.livemap.chart.*
import jetbrains.livemap.chart.Locator
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.length
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object Locator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (!target.contains(PieSpecComponent::class)) {
            return null
        }

        val chartElement = target.get<ChartElementComponent>()
        val pieSpec = target.get<PieSpecComponent>()
        val origin = target.get<WorldOriginComponent>().origin

        computeSectors(pieSpec, chartElement.scalingSizeFactor).forEach { sector ->
            if (isCoordinateInPieSector(
                    renderHelper.posToWorld(coord),
                    origin,
                    renderHelper.dimToWorld(sector.radius),
                    renderHelper.dimToWorld(sector.holeRadius),
                    sector.startAngle,
                    sector.endAngle
                )
            ) {
                return HoverObject(
                    layerIndex = target.get<IndexComponent>().layerIndex,
                    index = sector.index,
                    distance = 0.0,
                    this
                )
            }
        }

        return null
    }

    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.firstOrNull()

    private fun <T> isCoordinateInPieSector(
        p: Vec<T>,
        pieCenter: Vec<T>,
        pieRadius: Scalar<T>,
        holeRadius: Scalar<T>,
        startAngle: Double,
        endAngle: Double
    ): Boolean {
       val d = p - pieCenter
        if (d.length < holeRadius) return false
        if (d.length > pieRadius) return false

        val angle = atan2(d.y, d.x).let {
            when {
                it in -PI / 2..PI && abs(startAngle) > PI -> it - 2 * PI
                it in -PI..-PI / 2 && abs(endAngle) > PI -> it + 2 * PI
                else -> it
            }
        }
        return startAngle <= angle && angle < endAngle
    }
}