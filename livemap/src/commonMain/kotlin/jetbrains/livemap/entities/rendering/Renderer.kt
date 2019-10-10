package jetbrains.livemap.entities.rendering

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity

interface Renderer {
    fun render(entity: EcsEntity, ctx: Context2d)
}