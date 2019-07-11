package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        SceneMapperDemoFrame.showSvg(
            svgRoots,
            listOf("/svgMapper/jfx/plot.css"),
            demoComponentSize,
            "Axis component (JFX SVG mapper)"
        )
    }
}

