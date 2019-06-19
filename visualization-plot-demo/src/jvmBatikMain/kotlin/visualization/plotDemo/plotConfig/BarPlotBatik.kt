package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot

object BarPlotBatik {
    @JvmStatic
    fun main(args: Array<String>) {

        with(BarPlot()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Bar plot", plotSpecList, BatikMapperDemoFactory(), demoComponentSize)
        }
    }
}
