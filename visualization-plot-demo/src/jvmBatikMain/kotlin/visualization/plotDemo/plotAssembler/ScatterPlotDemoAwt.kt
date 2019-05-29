package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ScatterPlotDemo

class ScatterPlotDemoAwt : ScatterPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SwingDemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Scatter plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterPlotDemoAwt().show()
        }
    }
}
