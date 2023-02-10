/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity

interface LocatorHelper {
    fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean

    fun search(coord: Vec<Client>, target: EcsEntity): HoverObject? {
        if (isCoordinateInTarget(coord, target)) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index
            )
        }

        return null
    }
}
