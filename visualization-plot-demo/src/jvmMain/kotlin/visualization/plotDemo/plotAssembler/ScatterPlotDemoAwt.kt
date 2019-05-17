package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ScatterPlotDemo

class ScatterPlotDemoAwt : ScatterPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SwingDemoFrame.showSvg(svgRoots, demoComponentSize, "Scatter plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterPlotDemoAwt().show()
        }
    }
}
