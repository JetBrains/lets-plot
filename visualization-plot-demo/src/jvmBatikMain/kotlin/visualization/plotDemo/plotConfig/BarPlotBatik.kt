package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot

class BarPlotBatik : BarPlot() {

    private fun show() {
        val plotSpecList = listOf(
                basic().toMutableMap(),
                fancy().toMutableMap()
        )

        PlotConfigDemoUtil.show("Bar plot", plotSpecList, BatikMapperDemoFactory(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotBatik().show()
        }
    }
}
