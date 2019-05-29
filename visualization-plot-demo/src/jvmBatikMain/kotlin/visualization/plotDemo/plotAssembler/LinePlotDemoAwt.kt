package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.LinePlotDemo

class LinePlotDemoAwt : LinePlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SwingDemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Line plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LinePlotDemoAwt().show()
        }
    }
}
