/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swingCanvas

import org.jetbrains.letsPlot.awt.canvas.CanvasComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgDrawable
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class SvgViewerDemoWindowCanvas(
    title: String,
    private val svgRoots: List<SvgSvgElement>,
    maxCol: Int = 2,
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
            for (svgRoot in svgRoots) {
                val svgDrawable = SvgDrawable(svgRoot)
                val canvasComponent = CanvasComponent(svgDrawable)
                rootPanel.add(canvasComponent)
            }

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
            size = Dimension(600, 600)
        }
    }
}