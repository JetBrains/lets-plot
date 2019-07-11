package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.demoUtils.jfx.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeContinuousXDemoCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.continuousX(), CanvasRendererDemoFactory())
    }
}