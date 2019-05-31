package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeDiscreteXDemoBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), DemoFactoryBatik())
    }
}