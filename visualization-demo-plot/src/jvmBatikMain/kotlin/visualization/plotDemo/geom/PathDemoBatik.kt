package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.PathDemo

class PathDemoBatik : PathDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Path geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PathDemoBatik().show()
        }
    }
}
