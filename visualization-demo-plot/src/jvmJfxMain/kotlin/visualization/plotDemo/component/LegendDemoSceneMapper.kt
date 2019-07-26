package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plotDemo.model.component.LegendDemo

class LegendDemoSceneMapper : LegendDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendDemoSceneMapper().show()
        }
    }

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Legend component")
    }
}
