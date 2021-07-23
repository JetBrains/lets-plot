/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.JComponent

internal class CanvasComponent(
    private val canvas: AwtCanvas
) : JComponent() {
    init {
        bounds = Rectangle(0, 0, canvas.size.x, canvas.size.y)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        g!!.drawImage(canvas.image, 0, 0, canvas.size.x, canvas.size.y, this)
    }

    override fun isOpaque(): Boolean = true
}