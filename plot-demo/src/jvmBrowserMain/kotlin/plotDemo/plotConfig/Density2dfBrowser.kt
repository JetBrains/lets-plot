package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Density2df

object Density2dfBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigDemoUtil.show(
                "Density2df plot",
                plotSpecList() as List<MutableMap<String, Any>>,
                demoComponentSize
            ))
        }
    }
}
