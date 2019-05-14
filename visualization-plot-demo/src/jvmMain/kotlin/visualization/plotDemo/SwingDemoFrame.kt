package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgToAwt.SvgAwtComponent
import jetbrains.datalore.visualization.base.svgToAwt.SvgAwtHelper
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class SwingDemoFrame(
        private val title: String,
        private val size: Dimension = FRAME_SIZE) {

    fun show(initContent: JPanel.() -> Unit) {
        SwingUtilities.invokeLater {
            val frame = JFrame(title)

            val panel = JPanel()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(MARGIN_LEFT, 0)))

            panel.add(Box.createRigidArea(Dimension(0, SPACE_V)))

            panel.initContent()

            frame.add(JScrollPane(panel))

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.size = size
            frame.isVisible = true
        }
    }

    companion object {
        private val FRAME_SIZE = Dimension(800, 600)
        private const val MARGIN_LEFT = 50
        const val SPACE_V = 5

        fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector, title: String = "") {
            SwingDemoFrame(title).show {
                for (svgRoot in svgRoots) {
                    val component = createComponent(svgRoot)

                    component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

                    component.minimumSize = Dimension(size.x.toInt(), size.y.toInt())
                    component.maximumSize = Dimension(size.x.toInt(), size.y.toInt())
                    component.alignmentX = Component.LEFT_ALIGNMENT

                    add(Box.createRigidArea(Dimension(0, SPACE_V)))
                    add(component)
                }
            }

        }

        private fun createComponent(svgRoot: SvgSvgElement): JComponent {
            return object : SvgAwtComponent(svgRoot) {
                override fun createMessageCallback(): SvgAwtHelper.MessageCallback {
                    return createDefaultMessageCallback()
                }
            }
        }

    }
}