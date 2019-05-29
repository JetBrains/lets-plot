package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeDiscreteXDemoAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        BarPlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX())
    }
}