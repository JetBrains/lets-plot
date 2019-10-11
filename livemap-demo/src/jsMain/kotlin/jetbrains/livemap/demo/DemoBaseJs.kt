package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import kotlin.browser.document

class DemoBaseJs(private val demoModelProvider: (CanvasControl) -> DemoModelBase) {
    val size: Vector get() = Vector(800, 600)

    fun show() {
        val canvasControl = DomCanvasControl(size)
        demoModelProvider(canvasControl).show()

        document.getElementById(parentNodeId)
            ?.appendChild(canvasControl.rootElement)
            ?: error("Parent node '${parentNodeId}' wasn't found")
    }

    companion object {
        const val parentNodeId = "root"
    }
}