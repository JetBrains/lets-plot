package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.AxisOptions
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryCanvasRenderer

class AxisOptionsCanvasRenderer : AxisOptions() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>

        PlotConfigDemoUtil.show("Axis Options", plotSpecList, DemoFactoryCanvasRenderer(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisOptionsCanvasRenderer().show()
        }
    }
}
