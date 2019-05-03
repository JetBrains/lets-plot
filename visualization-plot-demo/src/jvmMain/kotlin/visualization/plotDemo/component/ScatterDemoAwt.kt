package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.SwingDemoUtil
import jetbrains.datalore.visualization.plotDemo.model.component.ScatterDemo

class ScatterDemoAwt : ScatterDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SwingDemoUtil.show(demoComponentSize, svgRoots)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScatterDemoAwt().show()
        }
    }
}
