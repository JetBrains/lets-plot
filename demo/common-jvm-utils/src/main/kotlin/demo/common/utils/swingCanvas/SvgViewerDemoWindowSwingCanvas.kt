/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swingCanvas

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Rectangle
import javax.swing.*
import kotlin.math.min

class SvgViewerDemoWindowSwingCanvas(
    title: String,
    private val svgRoots: List<SvgSvgElement>,
    private val maxCol: Int = 2,
) : JFrame("$title (Swing Canvas)") {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, svgRoots.size))
//        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)


        // Fixed plot size
        val scrollPane = JScrollPane(
            rootPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

        )
        contentPane.add(scrollPane)
    }

    fun open() {
        SwingUtilities.invokeLater {
            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
            size = Dimension(600, 600)
        }
    }

    private fun createWindowContent() {
        for (svgRoot in svgRoots) {
            rootPanel.add(createSvgPanel(svgRoot))
        }
    }

    private fun createSvgPanel(svgRoot: SvgSvgElement): JComponent {
        val panel = JPanel(null)
        val dim = Vector(
            svgRoot.width().get()?.toInt() ?: 800,
            svgRoot.height().get()?.toInt() ?: 600
        )

        val canvasControl = AwtCanvasControl(
            dim,
            animationTimerPeer = AwtAnimationTimerPeer(),
            mouseEventSource = AwtMouseEventMapper(panel)
        )

        val component = canvasControl.component()
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        component.bounds = Rectangle(0, 0, dim.x, dim.y)

        //val rootMapper = SvgSvgElementMapper(svgDocument, SvgSkiaPeer(fontManager))
        SvgCanvasFigure(svgRoot).mapToCanvas(canvasControl)

        return component
    }
}