/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.livemap.core.ecs.ComponentsList
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projection.World
import jetbrains.livemap.projection.WorldPoint

class MapIdComponent(val mapId: String) : EcsComponent

class RegionIdComponent(var regionId: String) : EcsComponent

class NeedCentroidComponent : EcsComponent
class WaitCentroidComponent : EcsComponent

class NeedLocationComponent : EcsComponent
class NeedGeocodeLocationComponent : EcsComponent
class WaitGeocodeLocationComponent : EcsComponent

class NeedCalculateLocationComponent : EcsComponent

class LocationComponent: EcsComponent {
    private var myWaitingCount: Int? = null
    val locations = ArrayList<Rect<World>>()

    fun add(rect: Rect<World>) {
        locations.add(rect)
    }

    fun wait(n: Int) {
        myWaitingCount = myWaitingCount?.let { it + n } ?: n
    }

    fun isReady(): Boolean {
        return myWaitingCount != null && myWaitingCount == locations.size
    }
}

class LonLatComponent(val point: LonLatPoint): EcsComponent

class WaitingGeocodingComponent : EcsComponent

class NeedBboxComponent : EcsComponent
class WaitBboxComponent : EcsComponent

class RegionBBoxComponent(val bbox: GeoRectangle) : EcsComponent

class PointInitializerComponent(val worldPointInitializer: ComponentsList.(worldPoint: WorldPoint) -> Unit): EcsComponent