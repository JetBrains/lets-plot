/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper

data class HoverObject(
    val layerIndex: Int,
    val index: Int,
    val distance: Double,
    val locator: Locator // TODO: move it out from HoverObject
)

interface Locator {
    fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject?
    fun reduce(hoverObjects: Collection<HoverObject>): HoverObject?
}
