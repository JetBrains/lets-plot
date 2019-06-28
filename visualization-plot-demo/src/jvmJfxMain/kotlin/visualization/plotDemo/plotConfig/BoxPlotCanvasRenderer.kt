package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BoxPlot
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
object BoxPlotCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BoxPlot()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Box plot", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
