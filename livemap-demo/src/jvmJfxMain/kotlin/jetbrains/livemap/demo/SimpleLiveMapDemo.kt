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

        //val canvas = canvasControl.createCanvas(VIEW_SIZE)
        //val ctx = canvas.context2d
//
        //ctx.setFillColor(Color.RED.toCssColor())
        //ctx.fillRect(0.0,0.0, VIEW_SIZE.x.toDouble(), VIEW_SIZE.y.toDouble())

        val reg = createLivemapModel(canvasControl)

        AwtCanvasDemoUtil.showAwtCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
