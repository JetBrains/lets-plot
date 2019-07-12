package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.browser.DomMapperDemoUtil.mapToDom
import jetbrains.datalore.visualization.plotDemo.model.component.AxisComponentDemo

/**
 * Called from generated HTML
 * Run with AxisComponentDemoBrowser.kt
 */
fun axisComponentDemo() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        mapToDom(svgRoots, "root")
    }
}

