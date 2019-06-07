package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelDemo

class TextLabelDemoAwt : TextLabelDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TextLabelDemoAwt().show()
        }
    }

    private fun show() {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        SvgMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Text label anchor and rotation")
    }
}
