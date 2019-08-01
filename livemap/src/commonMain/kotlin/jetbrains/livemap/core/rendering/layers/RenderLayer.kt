package jetbrains.livemap.core.rendering.layers

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.Context2d

class RenderLayer(private val myCanvas: Canvas, val name: String) {
    private val myRect = DoubleRectangle(0.0, 0.0, myCanvas.size.x.toDouble(), myCanvas.size.y.toDouble())
    private val myRenderTaskList = ArrayList<(Context2d) -> Unit>()

    val size: Vector
        get() = myCanvas.size

    fun addRenderTask(func: (Context2d) -> Unit) {
        myRenderTaskList.add(func)
    }

    fun render() {
        val context2d = myCanvas.context2d
        myRenderTaskList.forEach { it(context2d) }
        myRenderTaskList.clear()
    }

    fun takeSnapshot(): Async<Canvas.Snapshot> {
        return myCanvas.takeSnapshot()
    }

    fun clear() {
        myCanvas.context2d.clearRect(myRect)
    }
}