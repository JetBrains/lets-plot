package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Density2df

object Density2dfBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            PlotConfigDemoUtil.show("Density2df plot", plotSpecList() as List<MutableMap<String, Any>>, demoComponentSize)
        }
    }
}
