package jetbrains.datalore.maps.livemap.entities.rendering

import jetbrains.datalore.visualization.base.canvas.Context2d

object Common {

    fun apply(styleComponent: StyleComponent, ctx: Context2d) {
        ctx.setFillStyle(styleComponent.fillColor)
        ctx.setStrokeStyle(styleComponent.strokeColor)
        ctx.setLineWidth(styleComponent.strokeWidth)
    }
}
