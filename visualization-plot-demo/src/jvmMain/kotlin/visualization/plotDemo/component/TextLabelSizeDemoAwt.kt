package jetbrains.datalore.jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.gogProjectionalDemo.model.component.TextLabelSizeDemo
import visualization.plotDemo.SwingDemoUtil

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
        SwingDemoUtil.show(demoComponentSize, svgRoots)
    }
}
