/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.searching.HoverObject
import jetbrains.livemap.searching.Locator

object SearchTestHelper {
    const val UNDEFINED_SECTOR = -1

    private fun isTargetUnderCoord(mouseCoord: Vec<Client>, locator: Locator, target: EcsEntity): HoverObject? {
        return locator.search(mouseCoord, target)
    }

    fun getTargetUnderCoord(
        mouseCoord: Vec<Client>,
        locator: Locator,
        targets: List<EcsEntity>
    ): Int {
        for (i in targets.indices) {
            val result = isTargetUnderCoord(mouseCoord, locator, targets[i])
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