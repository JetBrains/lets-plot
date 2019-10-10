package jetbrains.datalore.plotDemo.plotContainer

import jetbrains.datalore.plotDemo.model.plotContainer.BarPlotResizeDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFactory

object BarPlotResizeDiscreteXDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), BatikMapperDemoFactory())
    }
}