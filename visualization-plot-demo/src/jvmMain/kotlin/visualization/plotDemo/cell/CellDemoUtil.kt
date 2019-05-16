package jetbrains.datalore.visualization.plotDemo.cell

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.gogProjectionalDemo.model.cell.BarPlotXAxisDemo
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame.Companion.createComponent
import java.awt.Color
import javax.swing.BorderFactory

object CellDemoUtil {
    @JvmStatic
    fun main(args: Array<String>) {
        show(BarPlotXAxisDemo.continuousX())
    }


    private fun show(demoModel: BarPlotXAxisDemo) {
        SwingDemoFrame("").show {
            val plot = demoModel.createPlot()
            plot.ensureContentBuilt()

            val svg = plot.svg

            val plotSizeProperty = demoModel.plotSize

            val viewportRect = SvgRectElement(DoubleRectangle(DoubleVector.ZERO, plotSizeProperty.get()))

            viewportRect.stroke().set(SvgColors.LIGHT_BLUE)
            viewportRect.fill().set(SvgColors.NONE)
            svg.children().add(viewportRect)


            val component = createComponent(svg)
            component.border = BorderFactory.createLineBorder(Color.BLUE, 1)
            add(component)

            SwingDemoFrame.addVSpace(this)

            val plotHeight = plotSizeProperty.get().y
            val widthControl = WidthControl(plotSizeProperty.get().x)
            add(widthControl)

            widthControl.width.addHandler(object : EventHandler<PropertyChangeEvent<out Double>> {
                override fun onEvent(event: PropertyChangeEvent<out Double>) {
                    val newWidth = event.newValue!!
                    val newSize = DoubleVector(newWidth, plotHeight)
                    plotSizeProperty.set(newSize)
                }
            })
        }
    }
}