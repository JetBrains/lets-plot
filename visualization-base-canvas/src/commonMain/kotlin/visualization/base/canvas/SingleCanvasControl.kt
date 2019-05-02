package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.geometry.Vector

class SingleCanvasControl(private val myCanvasControl: CanvasControl) {
    val canvas: Canvas

    val context: Context2d
        get() = canvas.context2d

    val size: Vector
        get() = myCanvasControl.size

    init {
        canvas = myCanvasControl.createCanvas(myCanvasControl.size)
        myCanvasControl.addChildren(canvas)
    }

    fun createCanvas(): Canvas {
        return myCanvasControl.createCanvas(myCanvasControl.size)
    }

    fun dispose() {
        myCanvasControl.removeChild(canvas)
    }
}
