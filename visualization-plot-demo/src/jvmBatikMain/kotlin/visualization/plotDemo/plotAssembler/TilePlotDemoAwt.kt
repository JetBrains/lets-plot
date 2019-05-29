package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.TilePlotDemo

class TilePlotDemoAwt : TilePlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SwingDemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Tile plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TilePlotDemoAwt().show()
        }
    }
}
