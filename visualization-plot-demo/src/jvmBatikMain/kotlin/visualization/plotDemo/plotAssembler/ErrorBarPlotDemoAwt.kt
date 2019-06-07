package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ErrorBarPlotDemo

class ErrorBarPlotDemoAwt : ErrorBarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SvgMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Error-bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ErrorBarPlotDemoAwt().show()
        }
    }
}
