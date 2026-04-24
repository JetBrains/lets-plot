/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.awt.*
import javax.swing.Icon

internal class PasteIcon(private val size: Int = 20) : Icon {
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val boardColor = Color(80, 80, 80)
        g2d.color = boardColor
        g2d.fillRoundRect(x + 2, y, size - 4, size, 6, 6)
        g2d.fillRect(x + 6, y - 1, size - 12, 5)
        g2d.color = Color(245, 245, 245)
        g2d.fillRect(x + 4, y + 4, size - 8, size - 7)
        g2d.stroke = BasicStroke(1.5f)
        g2d.color = boardColor
        g2d.drawLine(x + 5, y + 8, x + 14, y + 8)
        g2d.drawLine(x + 5, y + 11, x + 14, y + 11)
        g2d.drawLine(x + 5, y + 14, x + 14, y + 14)
        g2d.dispose()
    }

    override fun getIconWidth(): Int = size
    override fun getIconHeight(): Int = size
}
