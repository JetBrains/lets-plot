/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.MultiLineString
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.viewport.Viewport

object PathLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, viewport: Viewport): HoverObject? {
        if (!target.contains(WorldGeometryComponent::class)) {
            return null
        }

        val cursorMapCoord = viewport.getMapCoord(coord)
        val strokeRadius: Double = target.get<ChartElementComponent>().strokeWidth / 2
        if (isCoordinateInPath(
                cursorMapCoord,
                strokeRadius,
                target.get<WorldGeometryComponent>().geometry.multiLineString
            )
        ) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = 0.0,
                this
            )
        }


        return null
    }

    // Special logic is not yet determined.
    override fun reduce(hoverObjects: Collection<HoverObject>): HoverObject? = hoverObjects.firstOrNull()

    private fun isCoordinateInPath(
        coord: Vec<World>,
        strokeRadius: Double,
        multiLineString: MultiLineString<World>
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
}