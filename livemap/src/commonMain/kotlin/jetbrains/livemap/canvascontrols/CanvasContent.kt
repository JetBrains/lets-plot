package jetbrains.livemap.canvascontrols

import jetbrains.datalore.vis.canvas.CanvasControl

internal interface CanvasContent {
    fun show(parentControl: CanvasControl)
    fun hide()
}