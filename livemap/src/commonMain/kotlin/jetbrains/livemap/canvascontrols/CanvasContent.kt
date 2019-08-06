package jetbrains.livemap.canvascontrols

import jetbrains.datalore.visualization.base.canvas.CanvasControl

internal interface CanvasContent {
    fun show(parentControl: CanvasControl)
    fun hide()
}