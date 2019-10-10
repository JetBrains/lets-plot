package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Density2d

object Density2dBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2d()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigDemoUtil.show(
                "Density2d plot",
                plotSpecList() as List<MutableMap<String, Any>>,
                demoComponentSize
            ))
        }
    }
}
