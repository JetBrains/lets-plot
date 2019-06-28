package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.AxisOptions
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
object AxisOptionsCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(AxisOptions()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Axis Options", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
