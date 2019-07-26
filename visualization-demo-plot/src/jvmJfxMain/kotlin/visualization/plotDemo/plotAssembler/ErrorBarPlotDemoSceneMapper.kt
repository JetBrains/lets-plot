package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ErrorBarPlotDemo

class ErrorBarPlotDemoSceneMapper : ErrorBarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Error-bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ErrorBarPlotDemoSceneMapper().show()
        }
    }
}
