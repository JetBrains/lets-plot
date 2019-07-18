package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelSizeDemo

fun main() {
    with(TextLabelSizeDemo()) {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(
            svgRoots,
            listOf("/svgMapper/jfx/plot.css"),
            demoComponentSize,
            "Text label size and style"
        )
    }
}

