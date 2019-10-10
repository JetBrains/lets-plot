package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.AxisComponentDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Axis component (Batik)")
    }
}

