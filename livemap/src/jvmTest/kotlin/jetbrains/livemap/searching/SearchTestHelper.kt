/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.searching.HoverObject
import jetbrains.livemap.searching.Locator

object SearchTestHelper {
    private val viewport = Viewport(ViewportHelper(World.DOMAIN, true, myLoopY = false), ClientPoint(256, 256), 1, 15)
    const val UNDEFINED_SECTOR = -1

    private fun isTargetUnderCoord(mouseCoord: Vec<Client>, locator: Locator, target: EcsEntity): HoverObject? {
        return locator.search(mouseCoord, target, viewport)
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