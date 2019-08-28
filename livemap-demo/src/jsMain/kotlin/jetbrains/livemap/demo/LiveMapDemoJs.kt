package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.dom.DomCanvasControl
import jetbrains.livemap.demo.LivemapDemoModel.createLivemapModel
import kotlin.browser.document

@JsName("liveMapDemo")
fun liveMapDemo() {
    val parentNodeId = "root"
    val canvasControl = DomCanvasControl(Vector(800, 600))

    createLivemapModel(canvasControl)

    val rootElement = document.getElementById(parentNodeId)
        ?: throw IllegalStateException("Parent node '$parentNodeId' wasn't found")

    rootElement.appendChild(canvasControl.rootElement)
}