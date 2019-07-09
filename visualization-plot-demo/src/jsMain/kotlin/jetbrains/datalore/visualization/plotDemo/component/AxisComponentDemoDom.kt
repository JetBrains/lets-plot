package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo
import jetbrains.datalore.visualization.plotDemo.showSvg

/**
 * Called from generated HTML
 * Run with AxisComponentDemoBrowser.kt
 */
fun axisComponentDemo() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        showSvg(svgRoots, demoComponentSize)
    }
}

