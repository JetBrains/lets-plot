/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.embed.swing.JFXPanel
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFrame
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

object RawComponentDemo {
    @JvmStatic
    fun main(args: Array<String>) {

        val frame = JFrame("title")
        val panel = JPanel()
        panel.background = Color.WHITE
        panel.isOpaque = true
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(Box.createRigidArea(Dimension(50, 0)))
        panel.add(Box.createRigidArea(Dimension(0, SwingDemoFrame.SPACE_V)))

        frame.add(panel)

        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.size = Dimension(800, 600)
        frame.isVisible = true

        val livemap = object : JFXPanel() {
            override fun paint(g: Graphics?) {
                super.paint(g)
                g?.color = Color.BLUE
                g?.fill3DRect(50,50, 200,200, true)
            }
        }

        val tooltip = object : JFXPanel() {
            override fun paint(g: Graphics?) {
                super.paint(g)
                (g as Graphics2D).run {
                    color = Color.RED
                    fill3DRect(0,0, 50,50, true)
                }
            }
        }

        livemap.background = Color.GREEN
        livemap.bounds = Rectangle(0,0, 300, 300)
        livemap.isOpaque = true

        tooltip.background = Color.RED
        tooltip.bounds = Rectangle(10,10, 500, 500)
        tooltip.isOpaque = false



        tooltip.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                println("TOOLTIP")
            }
        })

        livemap.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                println("LIVEMAP")
            }
        })

        val container = JFXPanel()
        container.layout = null

        container.add(livemap)
        container.add(tooltip)

        container.setComponentZOrder(livemap, 1)
        container.setComponentZOrder(tooltip, 0)

        panel.add(Box.createRigidArea(Dimension(0, 5)))
        panel.add(container)
    }
}