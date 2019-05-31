package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.SwingDemoFactoryJfx
import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeDiscreteXDemoJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), SwingDemoFactoryJfx())
    }
}