/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import java.awt.Color
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class SvgViewerDemoWindowBatik(
    title: String,
    private val svgRoots: List<SvgSvgElement>,
    private val maxCol: Int = 2,
) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, svgRoots.size), 5, 5)
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
        }
    }

    private fun createWindowContent() {
        for (svgRoot in svgRoots) {
            rootPanel.add(createPlotComponent(svgRoot))
        }
    }

    private fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = BatikMapperComponent(svgRoot, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}