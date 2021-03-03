/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import java.awt.Dimension
import javax.swing.*

abstract class SwingDemoFrame(
    private val title: String,
    private val size: Dimension
) {

    fun show(scroll: Boolean = true, initContent: JPanel.() -> Unit) {
        SwingUtilities.invokeLater {
            val frame = JFrame(title)

            val panel = JPanel()
//            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(MARGIN_LEFT, 0)))
            panel.add(
                Box.createRigidArea(
                    Dimension(
                        0,
                        SPACE_V
                    )
                )
            )
            panel.initContent()

            if (scroll) {
                frame.add(JScrollPane(panel))
            } else {
                frame.add(panel)
            }

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.size = size
            frame.isVisible = true
        }
    }

    companion object {
        val FRAME_SIZE = Dimension(800, 1200)
        private const val MARGIN_LEFT = 50
        const val SPACE_V = 5

        private fun addVSpace(container: JPanel) {
            container.add(
                Box.createRigidArea(
                    Dimension(
                        0,
                        SPACE_V
                    )
                )
            )
        }
    }
}