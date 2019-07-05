package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Polygons

object PolygonsCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Polygons()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Polygon", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
