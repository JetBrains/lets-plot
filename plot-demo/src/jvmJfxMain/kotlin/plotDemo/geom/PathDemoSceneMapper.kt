package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.geom.PathDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class PathDemoSceneMapper : PathDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Path geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PathDemoSceneMapper().show()
        }
    }
}
