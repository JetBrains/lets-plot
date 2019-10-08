package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.Polygons

object PolygonsSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Polygons()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Polygon",
                plotSpecList,
                SceneMapperDemoFactory(JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}
