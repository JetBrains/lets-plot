package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.SceneMapperDemoFrame.Companion.showSvg
import jetbrains.datalore.visualization.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.visualization.plotDemo.model.component.TextLabelSizeDemo

fun main() {
    with(TextLabelSizeDemo()) {
        val demoModels = listOf(createModel())
        val svgRoots = createSvgRoots(demoModels)
        showSvg(
            svgRoots,
            listOf(JFX_PLOT_STYLESHEET),
            demoComponentSize,
            "Text label size and style"
        )
    }
}

