/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.core.ecs.ComponentsList
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.geocoding.LonLatComponent
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geocoding.PointInitializerComponent


fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: WorldRectangle): MultiPolygon<World> {
    return if (horizontal) {
        listOf(
            point.transform(
                newX = { mapRect.scalarLeft }
            ),
            point.transform(
                newX = { mapRect.scalarRight }
            )

        )
    } else {
        listOf(
            point.transform(
                newY = { mapRect.scalarTop }
            ),
            point.transform(
                newY = { mapRect.scalarBottom }
            )
        )
    }
        .run { listOf(Ring(this)) }
        .run { listOf(Polygon(this)) }
        .run { MultiPolygon(this) }
}

fun createLineBBox(
    point: WorldPoint,
    strokeWidth: Double,
    horizontal: Boolean,
    mapRect: WorldRectangle
): WorldRectangle {
    return if (horizontal) {
        WorldRectangle(
            explicitVec(mapRect.left, point.y - strokeWidth / 2),
            explicitVec(mapRect.width, strokeWidth)
        )
    } else {
        WorldRectangle(
            explicitVec(point.x - strokeWidth / 2, mapRect.top),
            explicitVec(strokeWidth, mapRect.height)
        )
    }
}

fun MapEntityFactory.createStaticEntityWithLocation(name: String, point: LonLatPoint): EcsEntity =
    createStaticEntity(name, point).addComponents {
        + NeedLocationComponent
        + NeedCalculateLocationComponent
    }

fun MapEntityFactory.createStaticEntity(name: String, point: LonLatPoint): EcsEntity =
    createMapEntity(name)
        .add(LonLatComponent(point))


internal fun EcsEntity.setInitializer(block: ComponentsList.(worldPoint: WorldPoint) -> Unit): EcsEntity {
    return add(PointInitializerComponent(block))
}