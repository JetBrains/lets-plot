package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

class AxisComponentDemoBatik : AxisComponentDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisComponentDemoBatik().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Axis component (Batik)")
    }
}
