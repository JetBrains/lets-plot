package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.DemoFrameBatik
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelSizeDemo

class TextLabelSizeDemoAwt : TextLabelSizeDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TextLabelSizeDemoAwt().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        DemoFrameBatik.showSvg(svgRoots, demoComponentSize, "Text label size and style")
    }
}
