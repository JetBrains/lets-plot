/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.MultiLineString
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent

object PathLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity): HoverObject? {
        if (!target.contains(LOCATABLE_COMPONENTS)) {
            return null
        }

        val strokeRadius: Double = target.get<ChartElementComponent>().strokeWidth / 2
        target.get<ScreenLoopComponent>().origins.forEach { origin ->
            if (isCoordinateInPath(
                    coord - origin,
                    strokeRadius,
                    target.get<ScreenGeometryComponent>().geometry.multiLineString
                )
            ) {
                return HoverObject(
                    layerIndex = target.get<IndexComponent>().layerIndex,
                    index = target.get<IndexComponent>().index,
                    distance = 0.0,
                    this
                )
            }
        }

        return null
    }

    // Special logic is not yet determined.
    override fun reduce(hoverObjects: Collection<HoverObject>): HoverObject? = hoverObjects.firstOrNull()

    private fun isCoordinateInPath(
        coord: Vec<Client>,
        strokeRadius: Double,
        multiLineString: MultiLineString<Client>
    ): Boolean {
        for (lineString in multiLineString) {
            val bbox = lineString.bbox ?: continue

            if (!LocatorUtil.coordInExtendedRect(coord, bbox, strokeRadius)) {
                continue
            }
            if (LocatorUtil.pathContainsCoordinate(coord, lineString, strokeRadius)) {
                return true
            }
        }
        return false
    }

    private val LOCATABLE_COMPONENTS = listOf(ScreenLoopComponent::class, ScreenGeometryComponent::class)
}