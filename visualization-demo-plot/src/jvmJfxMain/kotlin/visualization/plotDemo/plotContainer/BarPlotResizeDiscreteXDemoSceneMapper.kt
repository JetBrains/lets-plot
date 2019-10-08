package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeDiscreteXDemoSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET))
    }
}