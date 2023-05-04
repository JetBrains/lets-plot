/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.length
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.LocatorUtil.distance

object PointLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity): HoverObject? {
        if (REQUIRED_COMPONENTS !in target) {
            return null
        }

        val radius = target.get<PointComponent>().size / 2 * target.get<ChartElementComponent>().scalingSizeFactor
        return target.get<ScreenLoopComponent>().origins
            .singleOrNull { distance(coord, it) <= radius }
            ?.let {
                HoverObject(
                    layerIndex = target.get<IndexComponent>().layerIndex,
                    index = target.get<IndexComponent>().index,
                    distance = (it - coord).length,
                    this
                )
            }
    }

    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.minByOrNull(HoverObject::distance)

    private val REQUIRED_COMPONENTS =
        listOf(PointComponent::class, ScreenLoopComponent::class, ChartElementComponent::class)
}
