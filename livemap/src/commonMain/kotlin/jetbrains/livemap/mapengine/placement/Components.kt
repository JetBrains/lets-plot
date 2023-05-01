/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.placement

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.Client
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.core.ecs.EcsComponent
import kotlin.math.floor

class WorldDimensionComponent(var dimension: WorldPoint) : EcsComponent
class WorldOriginComponent(var origin: WorldPoint) : EcsComponent

class ScreenLoopComponent : EcsComponent {
    var origins: List<Vec<Client>> = ArrayList()
    var rounding: Rounding = Rounding.NONE

    enum class Rounding(private val f: (Vec<Client>) -> Vec<Client>) {
        NONE({ it }),
        FLOOR( { explicitVec(floor(it.x), floor(it.y)) });

        fun apply(vector: Vec<Client>) = f(vector)
    }
}

class ScreenDimensionComponent : EcsComponent {
    var dimension: Vec<Client> = Client.ZERO_VEC
}

class ScreenOriginComponent : EcsComponent {
    var origin: Vec<Client> = Client.ZERO_VEC
}
