package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.DemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.component.ScatterDemo

class ScatterDemoAwt : ScatterDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        DemoFrameBatik("Point geom with scale breaks and limits").showSvg(svgRoots, demoComponentSize)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterDemoAwt().show()
        }
    }
}
