package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

class AxisComponentDemoJfx : AxisComponentDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisComponentDemoJfx().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        CanvasRendererDemoFrame.showSvg(svgRoots, demoComponentSize, "Axis component")
    }
}
