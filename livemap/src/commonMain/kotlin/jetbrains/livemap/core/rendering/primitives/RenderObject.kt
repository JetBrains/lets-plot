package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.vis.canvas.Context2d

interface RenderObject {
    fun render(ctx: Context2d)
}