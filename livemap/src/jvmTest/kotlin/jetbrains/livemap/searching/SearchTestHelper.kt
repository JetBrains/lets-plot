/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.projection.Client
import jetbrains.livemap.searching.LocatorHelper

object SearchTestHelper {
    const val UNDEFINED_SECTOR = -1

    fun isTargetUnderCoord(mouseCoord: Vec<Client>, locatorHelper: LocatorHelper, target: EcsEntity): Boolean {
        return locatorHelper.isCoordinateInTarget(mouseCoord, target)
    }

    fun getTargetUnderCoord(
        mouseCoord: Vec<Client>,
        locatorHelper: LocatorHelper,
        targets: List<EcsEntity>
    ): Int {
        for (i in targets.indices) {
            if (isTargetUnderCoord(mouseCoord, locatorHelper, targets[i])) {
                return i
            }
        }
        return UNDEFINED_SECTOR
    }

    fun point(x: Int, y: Int): Vec<Client> {
        return explicitVec(x.toDouble(), y.toDouble())
    }
}