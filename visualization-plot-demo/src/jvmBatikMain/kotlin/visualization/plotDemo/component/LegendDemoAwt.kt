package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrameBatik
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
        SwingDemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Legend component")
    }
}
