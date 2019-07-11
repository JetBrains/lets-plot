package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.jfx.CanvasRendererDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        CanvasRendererDemoFrame.showSvg(svgRoots, demoComponentSize, "Axis component (Canvas renderer)")
    }
}

