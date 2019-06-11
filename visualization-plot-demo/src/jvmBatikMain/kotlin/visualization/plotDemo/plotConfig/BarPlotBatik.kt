package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarPlot
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryBatik

class BarPlotBatik : BarPlot() {

    private fun show() {
        val plotSpecList = listOf(
                basic().toMutableMap(),
                fancy().toMutableMap()
        )

//        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
        PlotConfigDemoUtil.show("Bar plot", plotSpecList, DemoFactoryBatik(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotBatik().show()
        }
    }
}
