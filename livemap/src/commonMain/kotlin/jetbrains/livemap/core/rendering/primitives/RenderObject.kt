package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.visualization.base.canvas.Context2d

interface RenderObject {
    fun render(ctx: Context2d)
}