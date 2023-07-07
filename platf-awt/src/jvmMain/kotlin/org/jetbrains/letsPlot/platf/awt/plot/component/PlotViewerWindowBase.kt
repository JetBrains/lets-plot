/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.plot.component

import java.awt.Color
import java.awt.Dimension
import javax.swing.*

abstract class PlotViewerWindowBase(
    title: String,
    private val windowSize: Dimension? = null,
) : JFrame(title) {

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    fun open() {
        SwingUtilities.invokeLater {
            val content = createWindowContent(
                preferredSizeFromPlot = windowSize == null
            )
            content.background = Color.WHITE
            content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            contentPane.add(content)

            if (windowSize != null) {
                this.size = windowSize
            } else {
                this.pack()
            }

            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    protected abstract fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent
}