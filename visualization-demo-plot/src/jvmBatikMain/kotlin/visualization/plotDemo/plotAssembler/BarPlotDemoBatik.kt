package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.BarPlotDemo

class BarPlotDemoBatik : BarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotDemoBatik().show()
        }
    }
}
