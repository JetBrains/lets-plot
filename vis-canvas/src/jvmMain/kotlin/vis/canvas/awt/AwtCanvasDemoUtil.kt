/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

object AwtCanvasDemoUtil {
    fun showCanvasControl(title: String, canvasControl: JavafxCanvasControl) {
        val frame = JFrame(title)
        frame.layout = BorderLayout()
        frame.contentPane.add(canvasControl.component)
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(canvasControl.size.x, canvasControl.size.y)
        frame.isVisible = true
    }
}
