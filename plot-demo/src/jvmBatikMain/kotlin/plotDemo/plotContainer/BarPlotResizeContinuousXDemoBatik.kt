package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFactory

object BarPlotResizeContinuousXDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.continuousX(), BatikMapperDemoFactory())
    }
}