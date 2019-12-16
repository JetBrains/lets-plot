/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.geospatial.GeoRectangle
import jetbrains.livemap.core.ecs.ComponentsList
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.WorldPoint

class MapIdComponent(val mapId: String) : EcsComponent

class RegionIdComponent(var regionId: String) : EcsComponent

class CentroidComponent : EcsComponent

class NeedLocationComponent : EcsComponent
class NeedGeocodeLocationComponent : EcsComponent
class WaitGeocodeLocationComponent : EcsComponent

class NeedCalculateLocationComponent : EcsComponent

class LocationComponent: EcsComponent {
    private var myWaitingCount = 0
    val locations = ArrayList<GeoRectangle>()

    fun add(rect: GeoRectangle) {
        locations.add(rect)
    }

    fun wait(n: Int) {
        myWaitingCount += n
    }

    fun isReady(): Boolean {
        return myWaitingCount > 0 && myWaitingCount == locations.size
    }
}

class LonLatComponent(val point: LonLatPoint): EcsComponent

class WaitingGeocodingComponent : EcsComponent

class RegionBBoxComponent(val bbox: GeoRectangle) : EcsComponent

class PointInitializerComponent(val worldPointInitializer: ComponentsList.(worldPoint: WorldPoint) -> Unit): EcsComponent