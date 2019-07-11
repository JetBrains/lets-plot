package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.DEMO_COMMON_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.KOTLIN_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.PLOT_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "visualization-plot-demo"
private const val CALL_FUN = "jetbrains.datalore.visualization.plotDemo.component.axisComponentDemo"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + PLOT_LIBS + DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Axis component")
    }
}
