/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.chart.HoverObject
import jetbrains.livemap.chart.Locator
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper

object SearchTestHelper {
    const val UNDEFINED_SECTOR = -1

    private fun isTargetUnderCoord(mouseCoord: Vec<Client>, locator: Locator, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        return locator.search(mouseCoord, target, renderHelper)
    }

    fun getTargetUnderCoord(
        mouseCoord: Vec<Client>,
        locator: Locator,
        targets: List<EcsEntity>,
        renderHelper: RenderHelper
    ): Int {
        for (i in targets.indices) {
            val result = isTargetUnderCoord(mouseCoord, locator, targets[i], renderHelper)
            if (result != null) {
                return result.index
            }
        }
        return UNDEFINED_SECTOR
    }
}