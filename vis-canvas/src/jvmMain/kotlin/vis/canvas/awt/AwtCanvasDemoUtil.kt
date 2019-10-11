package jetbrains.datalore.vis.canvas.awt

import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

object AwtCanvasDemoUtil {
    fun showAwtCanvasControl(title: String, canvasControl: AwtCanvasControl) {
        val frame = JFrame(title)
        frame.layout = BorderLayout()
        frame.contentPane.add(canvasControl.component)
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(canvasControl.size.x, canvasControl.size.y)
        frame.isVisible = true
    }
}
