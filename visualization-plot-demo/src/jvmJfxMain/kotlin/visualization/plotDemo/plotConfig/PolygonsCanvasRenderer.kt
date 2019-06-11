package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Polygons
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryCanvasRenderer

class PolygonsCanvasRenderer : Polygons() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>

        PlotConfigDemoUtil.show("Polygon", plotSpecList, DemoFactoryCanvasRenderer(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PolygonsCanvasRenderer().show()
        }
    }
}
