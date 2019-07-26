package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.RasterImagePlotDemo

class RasterImagePlotDemoSceneMapper : RasterImagePlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Raster image plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            RasterImagePlotDemoSceneMapper().show()
        }
    }
}
