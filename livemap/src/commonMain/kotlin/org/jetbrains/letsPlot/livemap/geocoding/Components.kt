/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geocoding

import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.core.ecs.ComponentsList
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

class RegionIdComponent(var regionId: String) : EcsComponent

object NeedLocationComponent : EcsComponent
object NeedGeocodeLocationComponent : EcsComponent

object NeedCalculateLocationComponent : EcsComponent

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
class PointInitializerComponent(val worldPointInitializer: ComponentsList.(worldPoint: WorldPoint) -> Unit): EcsComponent