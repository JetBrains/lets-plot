package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BarPlot
import jetbrains.datalore.vis.swing.BatikMapperDemoFactory

object BarPlotBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BarPlot()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Bar plot", plotSpecList,
                BatikMapperDemoFactory(), demoComponentSize)
        }
    }
}
