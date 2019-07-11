package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.demoUtils.jfx.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.ABLine

object ABLineCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ABLine()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("ABLine plot", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
