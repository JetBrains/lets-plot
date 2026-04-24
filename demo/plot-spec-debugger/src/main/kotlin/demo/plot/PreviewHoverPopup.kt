/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JWindow

internal class PreviewHoverPopup(owner: Window) {
    private val imageLabel = JLabel()

    private val window = JWindow(owner).apply {
        focusableWindowState = false
        type = Window.Type.POPUP
        contentPane.layout = BorderLayout()
        contentPane.background = Color.WHITE
        contentPane.add(imageLabel, BorderLayout.CENTER)
        (contentPane as javax.swing.JComponent).border =
            BorderFactory.createLineBorder(Color.DARK_GRAY, 1)
    }

    fun show(image: BufferedImage, anchor: Component, rowBounds: Rectangle) {
        imageLabel.icon = ImageIcon(image)
        window.pack()

        val anchorLoc = anchor.locationOnScreen
        val screen = anchor.graphicsConfiguration?.bounds ?: Rectangle(0, 0, 1920, 1080)

        // Prefer to the right of the anchor component.
        var x = anchorLoc.x + anchor.width + 8
        if (x + window.width > screen.x + screen.width) {
            // Fallback: to the left of the anchor.
            x = anchorLoc.x - window.width - 8
        }
        if (x < screen.x) x = screen.x + 4

        var y = anchorLoc.y + rowBounds.y + rowBounds.height / 2 - window.height / 2
        if (y < screen.y) y = screen.y + 4
        if (y + window.height > screen.y + screen.height) {
            y = screen.y + screen.height - window.height - 4
        }

        window.location = Point(x, y)
        if (!window.isVisible) window.isVisible = true
    }

    fun hide() {
        if (window.isVisible) window.isVisible = false
    }
}
