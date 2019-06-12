package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelDemo

class TextLabelDemoBatik : TextLabelDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TextLabelDemoBatik().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Text label anchor and rotation")
    }
}
