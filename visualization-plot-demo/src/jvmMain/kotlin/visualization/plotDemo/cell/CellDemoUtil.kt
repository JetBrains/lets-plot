package jetbrains.datalore.visualization.plotDemo.cell

import jetbrains.datalore.visualization.gogProjectionalDemo.model.cell.BarPlotXAxisDemo
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame.Companion.createComponent

object CellDemoUtil {
    @JvmStatic
    fun main(args: Array<String>) {
        show(BarPlotXAxisDemo.continuousX())
    }


    private fun show(demoModel: BarPlotXAxisDemo) {
        SwingDemoFrame("").show {
            val plot = demoModel.createPlot()
            plot.ensureContentBuilt()

            val component = createComponent(plot.svg)
            add(component)

            SwingDemoFrame.addVSpace(this)
            val widthControl = WidthControl(demoModel.plotSize.get().x)
            add(widthControl)
        }
    }
}