package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot

fun main() {
    with(BarPlot()) {
        @Suppress("UNCHECKED_CAST")
        PlotConfigDemoUtil.show("Bar plot", plotSpecList() as List<MutableMap<String, Any>>, demoComponentSize)
    }
}
