package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.ScatterPlotDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

class ScatterPlotDemoBatik : ScatterPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Scatter plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterPlotDemoBatik().show()
        }
    }
}
