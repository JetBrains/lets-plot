package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.DemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

class AxisComponentDemoAwt : AxisComponentDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisComponentDemoAwt().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        DemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Axis component")
    }
}
