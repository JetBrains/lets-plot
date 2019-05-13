package visualization.plotDemo.cell

import jetbrains.datalore.base.geometry.DoubleVector
import java.awt.Color
import java.awt.Dimension
import javax.swing.*

object CellDemoUtilAwt {
    private val FRAME_SIZE = Dimension(800, 600)
    private const val MARGIN_LEFT = 50
    private const val SPACE_V = 5

    @JvmStatic
    fun main(args: Array<String>) {
        show(DoubleVector.ZERO)
    }


    private fun show(demoComponentSize: DoubleVector) {
        show {


            val widthControl = WidthControl(600)
            add(widthControl)


        }
    }

    // ToDo: re-use in other AWT demos
    private fun show(initContent: JPanel.() -> Unit) {
        SwingUtilities.invokeLater {
            val frame = JFrame()

            val panel = JPanel()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(MARGIN_LEFT, 0)))

            panel.add(Box.createRigidArea(Dimension(0, SPACE_V)))

            panel.initContent()

            frame.add(JScrollPane(panel))

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.size = FRAME_SIZE
            frame.isVisible = true
        }
    }
}