package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot

object BarPlotCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BarPlot()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Bar plot", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}

