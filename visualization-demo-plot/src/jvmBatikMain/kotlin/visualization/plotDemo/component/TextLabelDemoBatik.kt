package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelDemo

fun main() {
    with(TextLabelDemo()) {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Text label anchor and rotation")
    }
}