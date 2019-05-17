package jetbrains.datalore.visualization.plotDemo.stat

import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.stat.BinDemo

class BinDemoAwt : BinDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SwingDemoFrame.showSvg(svgRoots, demoComponentSize, "Bin stat")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BinDemoAwt().show()
        }
    }
}
