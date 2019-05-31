package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeContinuousXDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.continuousX(), DemoFactoryJfx())
    }
}