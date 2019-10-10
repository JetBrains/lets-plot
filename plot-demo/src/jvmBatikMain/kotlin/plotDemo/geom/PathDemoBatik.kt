package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PathDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

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
