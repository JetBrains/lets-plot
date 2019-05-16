package jetbrains.datalore.visualization.gogDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvasControl
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxGraphicsCanvasControlFactory
import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

object SwingDemoUtil {
    fun show(viewSize: DoubleVector, plotSpecList: List<MutableMap<String, Any>>) {
        SwingUtilities.invokeLater {
            val frame = JFrame()

            val panel = JPanel()
            panel.background = Color.WHITE
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(Box.createRigidArea(Dimension(50, 0)))

            for (plotSpec in plotSpecList) {
                val component = createComponent(viewSize, plotSpec)

                component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

                component.minimumSize = Dimension(viewSize.x.toInt(), viewSize.y.toInt())
                component.maximumSize = Dimension(viewSize.x.toInt(), viewSize.y.toInt())
                component.alignmentX = Component.LEFT_ALIGNMENT

                panel.add(Box.createRigidArea(Dimension(0, 5)))
                panel.add(component)
            }

            panel.add(Box.createRigidArea(Dimension(0, 5)))
            frame.add(JScrollPane(panel))

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.setSize(800, 600)
            frame.isVisible = true
        }
    }

    private fun createComponent(viewSize: DoubleVector, plotSpec: MutableMap<String, Any>): JComponent {

        val plot = DemoAndTest.createPlot(plotSpec, false)
        val canvasControl = AwtCanvasControl(
                JavafxGraphicsCanvasControlFactory(2.0),
                Vector(viewSize.x.toInt(), viewSize.y.toInt())
        )

        PlotCanvasMapper(plot, canvasControl) { consumer ->
            { JavafxThreadConsumer(consumer).accept(it) }
        }

        val component = canvasControl.component
        component.background = Color.WHITE
        component.isFocusable = true
        component.preferredSize = Dimension(viewSize.x.toInt(), viewSize.y.toInt())
        return component
    }
}
