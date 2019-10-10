package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotConfig.AllColorScales
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFactory

object AllColorScalesSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(AllColorScales()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Color Scales",
                plotSpecList,
                SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}
