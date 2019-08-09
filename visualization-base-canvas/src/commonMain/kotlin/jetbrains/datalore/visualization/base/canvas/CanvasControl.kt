package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.geometry.Vector

interface CanvasControl : AnimationProvider, CanvasProvider, MouseEventSource {

    val size: Vector

    fun addChild(canvas: Canvas)

    fun removeChild(canvas: Canvas)
}
