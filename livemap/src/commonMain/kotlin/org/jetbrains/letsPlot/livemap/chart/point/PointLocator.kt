/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.point

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.chart.*
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent


object PointLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (REQUIRED_COMPONENTS !in target) {
            return null
        }

        val origin = target.get<WorldOriginComponent>().origin
        val pointComponent = target.get<PointComponent>()
        val chartElementComponent = target.get<ChartElementComponent>()
        val radius = renderHelper.dimToWorld(pointComponent.scaledRadius(chartElementComponent.scalingSizeFactor))

        val distance = (renderHelper.posToWorld(coord) - origin).length
        if (distance <= radius + renderHelper.dimToWorld(EXTRA_RADIUS)) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = 0.0,//renderHelper.dimToClient(distance).value,
                locator = this,
                targetPosition = renderHelper.worldToPos(origin).toDoubleVector(),
                targetRadius = renderHelper.dimToClient(radius).value
            )
        }
        return null
    }

    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.minByOrNull(HoverObject::distance)

    private val REQUIRED_COMPONENTS = listOf(PointComponent::class, ChartElementComponent::class)
    private val EXTRA_RADIUS = 6.0.px
}