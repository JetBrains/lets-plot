package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFactory

object BarPlotResizeDiscreteXDemoSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET))
    }
}