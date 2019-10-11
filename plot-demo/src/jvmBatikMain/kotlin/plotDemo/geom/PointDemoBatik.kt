package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PointDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

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
