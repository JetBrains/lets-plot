package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.ABLine
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryCanvasRenderer

class ABLineCanvasRenderer : ABLine() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>

        PlotConfigDemoUtil.show("ABLine plot", plotSpecList, DemoFactoryCanvasRenderer(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ABLineCanvasRenderer().show()
        }
    }
}
