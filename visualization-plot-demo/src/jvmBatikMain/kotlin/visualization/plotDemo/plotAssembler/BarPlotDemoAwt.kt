package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.base.swing.DemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.BarPlotDemo

class BarPlotDemoAwt : BarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        DemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotDemoAwt().show()
        }
    }
}
