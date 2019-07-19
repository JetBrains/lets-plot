package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelDemo

fun main() {
    with(TextLabelDemo()) {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(
            svgRoots,
            listOf("/text-label-demo.css"),
            demoComponentSize,
            "Text label anchor and rotation"
        )
    }
}

