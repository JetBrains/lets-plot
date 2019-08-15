package jetbrains.livemap.demo


import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasControl
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxGraphicsCanvasControlFactory
import jetbrains.livemap.demo.LivemapDemoModel.VIEW_SIZE
import jetbrains.livemap.demo.LivemapDemoModel.createLivemapModel
import javax.swing.SwingUtilities

object SimpleLiveMapDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater { show() }
    }

    private fun show() {
        val canvasControl = AwtCanvasControl(JavafxGraphicsCanvasControlFactory(1.0), VIEW_SIZE)

        val reg = createLivemapModel(canvasControl)

        AwtCanvasDemoUtil.showAwtCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
