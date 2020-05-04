/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.border.LineBorder

object PlotResizeDemoUtil {

    private const val PADDING = 20

    private fun setupContainer(container: JComponent) {
        container.removeAll()
//        container.border = EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        container.border = LineBorder(
            Color.LIGHT_GRAY,
            PADDING
        )
        container.layout = FlowLayout(FlowLayout.CENTER, 0, 0)
    }

    private fun toPlotSize(containerSize: Dimension) = DoubleVector(
        containerSize.width.toDouble() - 2 * PADDING,
        containerSize.height.toDouble() - 2 * PADDING
    )

    fun show(demoModel: BarPlotResizeDemo, swingFactory: SwingDemoFactory) {
        swingFactory.createDemoFrame("Fit in frame (try to resize)").show(false) {

            setupContainer(this)
            this.addComponentListener(object : ComponentAdapter() {
                private val eventCount: AtomicInteger = AtomicInteger(0)
                private var plotCreated = false
                private val plotSizeProp = ValueProperty(DoubleVector.ZERO)

                override fun componentResized(e: ComponentEvent) {
                    eventCount.incrementAndGet()

                    val executor: (() -> Unit) -> Unit = if (plotCreated) {
                        // Only needed for JavaFX
                        // Supposedly, Java FX has already been initialized at this time
                        swingFactory.createPlotEdtExecutor()
                    } else {
                        { runnable: () -> Unit -> SwingUtilities.invokeLater(runnable) }
                    }

                    executor {
                        if (eventCount.decrementAndGet() == 0) {
                            val container = e.component as JComponent
                            container.invalidate()

                            // existing plot will be updated here
                            val newPlotSize =
                                toPlotSize(e.component.size)
                            plotSizeProp.set(newPlotSize)
                            if (!plotCreated) {
                                plotCreated = true
                                container.add(
                                    createPlot(
                                        demoModel,
                                        plotSizeProp,
                                        swingFactory
                                    )
                                )
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
        factory: SwingDemoFactory
    ): JComponent {
        val plot = demo.createPlot(plotSizeProp)

        return MonolithicAwt.buildPlotComponent(
            plot,
            factory::createSvgComponent,
            factory.createPlotEdtExecutor()
        )
    }
}