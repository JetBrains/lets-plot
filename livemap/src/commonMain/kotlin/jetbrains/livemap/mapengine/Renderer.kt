/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import org.jetbrains.letsPlot.core.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity

interface Renderer {
    fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper)
}