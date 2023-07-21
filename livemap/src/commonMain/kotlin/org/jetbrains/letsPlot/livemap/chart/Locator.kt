/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper

data class HoverObject(
    val layerIndex: Int,
    val index: Int,
    val distance: Double,
    val locator: Locator // TODO: move it out from HoverObject
)

interface Locator {
    fun search(coord: Vec<org.jetbrains.letsPlot.livemap.Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject?
    fun reduce(hoverObjects: Collection<HoverObject>): HoverObject?
}
