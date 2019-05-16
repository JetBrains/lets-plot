package visualization.plotDemo.cell

import jetbrains.datalore.visualization.gogProjectionalDemo.model.cell.BarPlotResizeDemo
import jetbrains.datalore.visualization.plotDemo.cell.BarPlotResizeDemoUtil

object BarPlotResizeContinuousXDemoAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        BarPlotResizeDemoUtil.show(BarPlotResizeDemo.continuousX())
    }
}