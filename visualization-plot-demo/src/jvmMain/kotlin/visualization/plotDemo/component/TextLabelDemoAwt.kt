package jetbrains.datalore.jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelDemo
import visualization.plotDemo.SwingDemoUtil

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
        SwingDemoUtil.show(demoComponentSize, svgRoots)
    }
}
