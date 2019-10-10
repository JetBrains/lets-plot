package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.base.event.MouseEventSpec.MOUSE_LEFT
import jetbrains.datalore.base.event.MouseEventSpec.MOUSE_MOVED
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.vis.svg.SvgColors
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.border.LineBorder

object PlotResizeDemoUtil {

    private const val PADDING = 20

    private fun setupContainer(container: JComponent) {
        container.removeAll()
//        container.border = EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        container.border = LineBorder(Color.LIGHT_GRAY, PADDING)
        container.layout = FlowLayout(FlowLayout.CENTER, 0, 0)
    }

    private fun toPlotSize(containerSize: Dimension) = DoubleVector(
        containerSize.width.toDouble() - 2 * PADDING,
        containerSize.height.toDouble() - 2 * PADDING
    )

    fun show(demoModel: BarPlotResizeDemo, factory: SwingDemoFactory) {
        factory.createDemoFrame("Fit in frame (try to resize)").show(false) {

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
                                createPlot(demoModel, plotSizeProp, container, factory)
                            }

                            container.revalidate()
//                            container.repaint()
                        }
                    }
                }
            })
        }
    }

    private fun createPlot(
        demo: BarPlotResizeDemo,
        plotSizeProp: ReadableProperty<DoubleVector>,
        container: JComponent,
        factory: SwingDemoFactory
    ) {

        val plot = demo.createPlot(plotSizeProp)
        plot.ensureContentBuilt()
        val svg = plot.svg

        // make a blue frame
        val frameRect =
            SvgRectElement(DoubleRectangle(DoubleVector.ZERO, plotSizeProp.get()))
        frameRect.stroke().set(SvgColors.LIGHT_CORAL)
        frameRect.fill().set(SvgColors.NONE)
        svg.children().add(frameRect)

        val component = factory.createSvgComponent(svg)
        container.add(component)

        plotSizeProp.addHandler(object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
            override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                frameRect.width().set(event.newValue!!.x)
                frameRect.height().set(event.newValue!!.y)
            }
        })

        // Bind mouse events
        val plotEdt = factory.createPlotEdtExecutor()
        component.addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent) {
                super.mouseExited(e)
                plotEdt {
                    plot.mouseEventPeer.dispatch(MOUSE_LEFT, AwtEventUtil.translate(e))
                }
            }
        })
        component.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                plotEdt {
                    plot.mouseEventPeer.dispatch(MOUSE_MOVED, AwtEventUtil.translate(e))
                }
            }
        })
    }
}