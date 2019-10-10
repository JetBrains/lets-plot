package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TextLabelSizeDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

fun main() {
    with(TextLabelSizeDemo()) {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Text label size and style")
    }
}