package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.Area

object AreaBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigDemoUtil.show(
                "Area plot",
                plotSpecList() as List<MutableMap<String, Any>>,
                demoComponentSize
            ))
        }
    }
}
