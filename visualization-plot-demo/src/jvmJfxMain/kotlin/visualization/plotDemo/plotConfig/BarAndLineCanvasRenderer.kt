package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarAndLine
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
object BarAndLineCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BarAndLine()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Bar & Line plot",
                plotSpecList,
                CanvasRendererDemoFactory(),
                demoComponentSize
            )
        }
    }
}

