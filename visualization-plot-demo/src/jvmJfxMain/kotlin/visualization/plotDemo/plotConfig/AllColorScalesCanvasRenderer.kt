package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.plotDemo.model.plotConfig.AllColorScales
import jetbrains.datalore.visualization.plotDemo.plotContainer.DemoFactoryCanvasRenderer

class AllColorScalesCanvasRenderer : AllColorScales() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
        PlotConfigDemoUtil.show("Color Scales", plotSpecList, DemoFactoryCanvasRenderer(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AllColorScalesCanvasRenderer().show()
        }
    }
}
