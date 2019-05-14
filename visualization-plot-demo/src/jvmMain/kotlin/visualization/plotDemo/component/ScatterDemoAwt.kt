package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.ScatterDemo

class ScatterDemoAwt : ScatterDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SwingDemoFrame.showSvg(svgRoots, demoComponentSize, "Point geom with scale breaks and limits")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterDemoAwt().show()
        }
    }
}
