package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame
import jetbrains.datalore.visualization.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        SceneMapperDemoFrame.showSvg(
            svgRoots,
            listOf(JFX_PLOT_STYLESHEET),
            demoComponentSize,
            "Axis component (JFX SVG mapper)"
        )
    }
}

