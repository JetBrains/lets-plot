/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.searching.LocatorHelper
import jetbrains.livemap.searching.SearchResult

object SearchTestHelper {
    const val UNDEFINED_SECTOR = -1

    private fun isTargetUnderCoord(mouseCoord: Vec<Client>, locatorHelper: LocatorHelper, target: EcsEntity): SearchResult? {
        return locatorHelper.search(mouseCoord, target)
    }

    fun getTargetUnderCoord(
        mouseCoord: Vec<Client>,
        locatorHelper: LocatorHelper,
        targets: List<EcsEntity>
    ): Int {
        for (i in targets.indices) {
            val result = isTargetUnderCoord(mouseCoord, locatorHelper, targets[i])
            if (result != null) {
                return result.index
            }
        }
        return UNDEFINED_SECTOR
    }

    fun point(x: Int, y: Int): Vec<Client> {
        return explicitVec(x.toDouble(), y.toDouble())
    }
}