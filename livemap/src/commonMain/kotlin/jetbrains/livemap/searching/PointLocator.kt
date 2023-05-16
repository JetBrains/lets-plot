/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.length
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport

object PointLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, viewport: Viewport): HoverObject? {
        if (REQUIRED_COMPONENTS !in target) {
            return null
        }

        val cursorMapCoord = viewport.getMapCoord(coord)
        val origin = target.get<WorldOriginComponent>().origin
        val radius = target.get<PointComponent>().size / 2 * target.get<ChartElementComponent>().scalingSizeFactor
        val worldRadius = viewport.toWorldDimension(ClientPoint(radius, radius))

        val distance = (cursorMapCoord - origin)
        if (distance.length <= worldRadius.x) {
            return HoverObject(
                        layerIndex = target.get<IndexComponent>().layerIndex,
                        index = target.get<IndexComponent>().index,
                        distance = viewport.toClientDimension(distance).length,
                        this
                    )

        }
        return null
        //return target.get<ScreenLoopComponent>().origins
        //    .singleOrNull { distance(coord, it) <= radius }
        //    ?.let {
        //        HoverObject(
        //            layerIndex = target.get<IndexComponent>().layerIndex,
        //            index = target.get<IndexComponent>().index,
        //            distance = (it - coord).length,
        //            this
        //        )
        //    }
    }

    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.minByOrNull(HoverObject::distance)

    private val REQUIRED_COMPONENTS =
        listOf(PointComponent::class, ChartElementComponent::class)
}
