package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Axis component (Batik)")
    }
}

