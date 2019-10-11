package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.BarDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

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
