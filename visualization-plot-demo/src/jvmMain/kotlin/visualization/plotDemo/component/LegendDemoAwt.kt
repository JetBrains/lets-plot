package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.SwingDemoUtil
import jetbrains.datalore.visualization.plotDemo.model.component.LegendDemo

class LegendDemoAwt : LegendDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendDemoAwt().show()
        }
    }

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SwingDemoUtil.show(demoComponentSize, svgRoots)
    }
}
