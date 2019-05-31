package jetbrains.datalore.visualization.base.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

abstract class SwingDemoFrame(
        private val title: String,
        private val size: Dimension) {

    fun show(scroll: Boolean = true, initContent: JPanel.() -> Unit) {
        SwingUtilities.invokeLater {
            val frame = JFrame(title)

            val panel = JPanel()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(MARGIN_LEFT, 0)))

            panel.add(Box.createRigidArea(Dimension(0, SPACE_V)))

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

    fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector) {
        show {
            for (svgRoot in svgRoots) {
                val component = createSvgComponent(svgRoot)

                component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

                component.minimumSize = Dimension(size.x.toInt(), size.y.toInt())
                component.maximumSize = Dimension(size.x.toInt(), size.y.toInt())
                component.alignmentX = Component.LEFT_ALIGNMENT

                addVSpace(this)
                add(component)
            }
        }
    }

    abstract fun createSvgComponent(svgRoot: SvgSvgElement): JComponent


    companion object {
        val FRAME_SIZE = Dimension(800, 600)
        private const val MARGIN_LEFT = 50
        const val SPACE_V = 5

        private fun addVSpace(container: JPanel) {
            container.add(Box.createRigidArea(Dimension(0, SPACE_V)))
        }
    }
}