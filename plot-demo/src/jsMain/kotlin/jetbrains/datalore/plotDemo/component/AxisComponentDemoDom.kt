package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.AxisComponentDemo
import jetbrains.datalore.vis.browser.DomMapperDemoUtil.mapToDom

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

