package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Histogram
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
object HistogramCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Histogram()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Histogram", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
