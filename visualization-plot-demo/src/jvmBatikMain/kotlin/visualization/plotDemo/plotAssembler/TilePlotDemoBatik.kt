package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.TilePlotDemo

class TilePlotDemoBatik : TilePlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Tile plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TilePlotDemoBatik().show()
        }
    }
}
