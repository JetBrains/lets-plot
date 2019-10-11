package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.geom.BarDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class BarDemoSceneMapper : BarDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Bar geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarDemoSceneMapper().show()
        }
    }
}
