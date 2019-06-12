package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.AllColorScales

class AllColorScalesCanvasRenderer : AllColorScales() {

    private fun show() {
        @Suppress("UNCHECKED_CAST")
        val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
        PlotConfigDemoUtil.show("Color Scales", plotSpecList, CanvasRendererDemoFactory(), this.demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AllColorScalesCanvasRenderer().show()
        }
    }
}
