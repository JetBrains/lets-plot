package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeContinuousXDemoAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        BarPlotResizeDemoUtil.show(BarPlotResizeDemo.continuousX())
    }
}