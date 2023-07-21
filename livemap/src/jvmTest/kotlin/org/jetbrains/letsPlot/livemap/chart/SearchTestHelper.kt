/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.chart.Locator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper

object SearchTestHelper {
    const val UNDEFINED_SECTOR = -1

    private fun isTargetUnderCoord(mouseCoord: Vec<org.jetbrains.letsPlot.livemap.Client>, locator: Locator, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        return locator.search(mouseCoord, target, renderHelper)
    }

    fun getTargetUnderCoord(
        mouseCoord: Vec<org.jetbrains.letsPlot.livemap.Client>,
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