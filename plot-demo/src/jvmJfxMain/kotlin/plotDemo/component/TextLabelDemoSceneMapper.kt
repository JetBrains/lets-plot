package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TextLabelDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

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

