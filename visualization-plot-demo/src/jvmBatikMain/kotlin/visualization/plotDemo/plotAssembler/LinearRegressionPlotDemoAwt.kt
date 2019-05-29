package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.LinearRegressionPlotDemo

class LinearRegressionPlotDemoAwt : LinearRegressionPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SwingDemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Linear regression plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LinearRegressionPlotDemoAwt().show()
        }
    }
}
