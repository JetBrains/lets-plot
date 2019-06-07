package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.PathDemo

class PathDemoAwt : PathDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SvgMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Path geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PathDemoAwt().show()
        }
    }
}
