package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.BarAndLine
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryCanvasRenderer

class BarAndLineCanvasRenderer : BarAndLine() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>

        PlotConfigDemoUtil.show("Bar & Line plot", plotSpecList, DemoFactoryCanvasRenderer(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarAndLineCanvasRenderer().show()
        }
    }
}
