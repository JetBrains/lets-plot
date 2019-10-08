package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.ScatterDemo

class ScatterDemoSceneMapper : ScatterDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Point geom with scale breaks and limits")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterDemoSceneMapper().show()
        }
    }
}
