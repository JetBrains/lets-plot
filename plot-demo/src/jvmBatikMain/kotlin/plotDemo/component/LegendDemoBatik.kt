package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.LegendDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

class LegendDemoBatik : LegendDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendDemoBatik().show()
        }
    }

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Legend component")
    }
}
