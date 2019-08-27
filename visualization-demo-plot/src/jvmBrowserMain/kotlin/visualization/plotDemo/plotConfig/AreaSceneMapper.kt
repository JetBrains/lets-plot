package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Area

object AreaSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            PlotConfigDemoUtil.show("Area plot", plotSpecList() as List<MutableMap<String, Any>>, demoComponentSize)
        }
    }
}
