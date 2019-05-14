package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
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
        SwingDemoFrame.showSvg(svgRoots, demoComponentSize, "Legend component")
    }
}
