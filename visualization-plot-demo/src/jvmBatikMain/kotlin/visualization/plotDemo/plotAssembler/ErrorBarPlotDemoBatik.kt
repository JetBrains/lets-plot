package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ErrorBarPlotDemo

class ErrorBarPlotDemoBatik : ErrorBarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Error-bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ErrorBarPlotDemoBatik().show()
        }
    }
}
