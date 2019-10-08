package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.ABLine

object ABLineSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ABLine()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "ABLine plot",
                plotSpecList,
                SceneMapperDemoFactory(JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}
