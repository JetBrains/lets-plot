package jetbrains.datalore.visualization.plotDemo.plotConfig

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFactory
import jetbrains.datalore.visualization.plotDemo.model.plotConfig.AllColorScales
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
object AllColorScalesCanvasRenderer {
    @JvmStatic
    fun main(args: Array<String>) {
        with(AllColorScales()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show("Color Scales", plotSpecList, CanvasRendererDemoFactory(), demoComponentSize)
        }
    }
}
