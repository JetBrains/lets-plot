package jetbrains.datalore.visualization.plotDemo.cell

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.gogProjectionalDemo.model.cell.BarPlotResizeDemo
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame.Companion.createSvgComponent
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.border.LineBorder

object BarPlotResizeDemoUtil {

    private const val PADDING = 20

    private fun setupContainer(container: JComponent) {
        container.removeAll()
//        container.border = EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        container.border = LineBorder(Color.LIGHT_GRAY, PADDING)
        container.layout = FlowLayout(FlowLayout.CENTER, 0, 0)
    }

    private fun toPlotSize(containerSize: Dimension) = DoubleVector(
            containerSize.width.toDouble() - 2 * PADDING,
            containerSize.height.toDouble() - 2 * PADDING)

    fun show(demoModel: BarPlotResizeDemo) {
        SwingDemoFrame("Fit in frame (try to resize)").show(false) {

            //            this.background = Color.BLUE
            setupContainer(this)
            this.addComponentListener(object : ComponentAdapter() {
                private val eventCount: AtomicInteger = AtomicInteger(0)
                private var plotCreated = false
                private val plotSizeProp = ValueProperty(DoubleVector.ZERO)
                override fun componentResized(e: ComponentEvent) {
                    eventCount.incrementAndGet()
                    SwingUtilities.invokeLater {
                        if (eventCount.decrementAndGet() == 0) {
                            val container = e.component as JComponent
                            container.invalidate()

                            // existing plot will be updated here
                            val newPlotSize = toPlotSize(e.component.size)
                            plotSizeProp.set(newPlotSize)
                            if (!plotCreated) {
                                plotCreated = true
                                createPlot(demoModel, plotSizeProp, container)
                            }

                            container.revalidate()
//                            container.repaint()
                        }
                    }
                }
            })
        }
    }

    private fun createPlot(demo: BarPlotResizeDemo, plotSizeProp: ReadableProperty<DoubleVector>, container: JComponent) {
        val plot = demo.createPlot(plotSizeProp)
        plot.ensureContentBuilt()
        val svg = plot.svg

        // make a blue frame
        val frameRect = SvgRectElement(DoubleRectangle(DoubleVector.ZERO, plotSizeProp.get()))
        frameRect.stroke().set(SvgColors.LIGHT_CORAL)
        frameRect.fill().set(SvgColors.NONE)
        svg.children().add(frameRect)

        val component = createSvgComponent(svg)
//        component.border = BorderFactory.createLineBorder(Color.BLUE, 1)
        container.add(component)

        plotSizeProp.addHandler(object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
            override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                frameRect.width().set(event.newValue!!.x)
                frameRect.height().set(event.newValue!!.y)
            }
        })
    }
}