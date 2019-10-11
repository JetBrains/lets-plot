package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.component.ScatterDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

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
