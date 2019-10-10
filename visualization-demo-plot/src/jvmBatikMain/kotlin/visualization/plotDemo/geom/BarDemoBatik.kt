package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.BarDemo

class BarDemoBatik : BarDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Bar geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarDemoBatik().show()
        }
    }
}
