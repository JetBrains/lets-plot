/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.viewport.Viewport

interface Locator {
    fun search(coord: Vec<Client>, target: EcsEntity, viewport: Viewport): HoverObject?
    fun reduce(hoverObjects: Collection<HoverObject>): HoverObject?
}
