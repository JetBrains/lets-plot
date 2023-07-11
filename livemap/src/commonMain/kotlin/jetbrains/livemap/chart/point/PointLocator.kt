/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.point

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.compareTo
import org.jetbrains.letsPlot.commons.intern.typedGeometry.length
import org.jetbrains.letsPlot.commons.intern.typedGeometry.minus
import jetbrains.livemap.Client
import jetbrains.livemap.chart.*
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

object PointLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (REQUIRED_COMPONENTS !in target) {
            return null
        }

        val origin = target.get<WorldOriginComponent>().origin
        val radius = renderHelper.dimToWorld(target.get<PointComponent>().size * target.get<ChartElementComponent>().scalingSizeFactor / 2)

        val distance = (renderHelper.posToWorld(coord) - origin).length
        if (distance <= radius) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = renderHelper.dimToClient(distance).value,
                this
            )
        }
        return null
    }

    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.minByOrNull(HoverObject::distance)

    private val REQUIRED_COMPONENTS = listOf(PointComponent::class, ChartElementComponent::class)
}