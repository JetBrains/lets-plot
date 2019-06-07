package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.ScatterDemo

class ScatterDemoAwt : ScatterDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SvgMapperDemoFrame("Point geom with scale breaks and limits").showSvg(svgRoots, demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterDemoAwt().show()
        }
    }
}
