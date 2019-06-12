package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

class AxisComponentDemoSceneMapper : AxisComponentDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisComponentDemoSceneMapper().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf("/svgMapper/jfx/plot.css"), demoComponentSize, "Axis component (JFX SVG mapper)")
    }
}
