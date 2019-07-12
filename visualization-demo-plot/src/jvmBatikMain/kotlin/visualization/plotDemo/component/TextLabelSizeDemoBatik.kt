package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelSizeDemo

class TextLabelSizeDemoBatik : TextLabelSizeDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TextLabelSizeDemoBatik().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Text label size and style")
    }
}
