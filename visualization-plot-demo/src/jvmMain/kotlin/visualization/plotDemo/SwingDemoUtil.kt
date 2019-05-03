package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgToAwt.SvgAwtComponent
import jetbrains.datalore.visualization.base.svgToAwt.SvgAwtHelper.MessageCallback
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*


object SwingDemoUtil {
    private val FRAME_SIZE = Dimension(800, 600)
    private const val MARGIN_LEFT = 50
    private const val SPACE_V = 5

    fun show(demoComponentSize: DoubleVector, svgRoots: List<SvgSvgElement>) {
        SwingUtilities.invokeLater {
            val frame = JFrame()

            val panel = JPanel()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(MARGIN_LEFT, 0)))

            for (svgRoot in svgRoots) {
                val component = createComponent(svgRoot)

                component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

                component.minimumSize = Dimension(demoComponentSize.x.toInt(), demoComponentSize.y.toInt())
                component.maximumSize = Dimension(demoComponentSize.x.toInt(), demoComponentSize.y.toInt())
                component.alignmentX = Component.LEFT_ALIGNMENT

                panel.add(Box.createRigidArea(Dimension(0, SPACE_V)))
                panel.add(component)
            }

            panel.add(Box.createRigidArea(Dimension(0, SPACE_V)))
            frame.add(JScrollPane(panel))

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.size = FRAME_SIZE
            frame.isVisible = true
        }
    }

    private fun createComponent(svgRoot: SvgSvgElement): JComponent {
        return object : SvgAwtComponent(svgRoot) {
            override fun createMessageCallback(): MessageCallback {
                return createDefaultMessageCallback()
            }
        }
    }
}
