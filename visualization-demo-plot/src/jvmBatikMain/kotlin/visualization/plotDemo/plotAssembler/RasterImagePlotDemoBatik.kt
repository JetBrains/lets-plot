package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.RasterImagePlotDemo

class RasterImagePlotDemoBatik : RasterImagePlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Raster image plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            RasterImagePlotDemoBatik().show()
        }
    }
}
