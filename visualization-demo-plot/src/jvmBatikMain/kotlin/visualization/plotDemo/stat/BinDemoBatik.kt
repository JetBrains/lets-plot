package jetbrains.datalore.visualization.plotDemo.stat

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.stat.BinDemo

class BinDemoBatik : BinDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Bin stat")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BinDemoBatik().show()
        }
    }
}
