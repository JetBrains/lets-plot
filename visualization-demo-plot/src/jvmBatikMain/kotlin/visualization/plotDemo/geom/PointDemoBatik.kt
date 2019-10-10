package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.PointDemo

class PointDemoBatik : PointDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Point geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PointDemoBatik().show()
        }
    }
}
