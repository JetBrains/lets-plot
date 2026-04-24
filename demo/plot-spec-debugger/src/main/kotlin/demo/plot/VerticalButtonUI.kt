/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.AbstractButton
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicButtonUI

internal class VerticalButtonUI : BasicButtonUI() {
    override fun paint(g: Graphics, c: JComponent) {
        val button = c as AbstractButton
        super.paint(g, c)

        val text = button.text
        if (text == null || text.isEmpty()) return

        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = button.foreground
        g2d.font = button.font

        val fm = g2d.fontMetrics
        val lines = text.split("\n")
        val lineHeight = fm.height
        val totalHeight = lines.size * lineHeight

        var y = (c.height - totalHeight) / 2 + fm.ascent
        for (line in lines) {
            val stringWidth = fm.stringWidth(line)
            val x = (c.width - stringWidth) / 2
            g2d.drawString(line, x, y)
            y += lineHeight
        }
        g2d.dispose()
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        val text = (c as AbstractButton).text ?: return Dimension(20, 100)
        val fm = c.getFontMetrics(c.font)
        val lines = text.split("\n")
        var maxWidth = 0
        for (line in lines) {
            maxWidth = kotlin.math.max(maxWidth, fm.stringWidth(line))
        }
        val totalHeight = fm.height * lines.size
        val insets = c.insets
        return Dimension(
            maxWidth + insets.left + insets.right,
            totalHeight + insets.top + insets.bottom
        )
    }
}
