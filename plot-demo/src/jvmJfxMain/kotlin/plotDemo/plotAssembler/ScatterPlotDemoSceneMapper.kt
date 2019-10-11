package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotAssembler.ScatterPlotDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class ScatterPlotDemoSceneMapper : ScatterPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Scatter plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterPlotDemoSceneMapper().show()
        }
    }
}
