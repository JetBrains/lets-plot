package jetbrains.datalore.visualization.plotDemo.component

import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.BASE_MAPPER_LIBS_JS
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.KOTLIN_LIBS_JS
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.PLOT_LIBS_JS
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "visualization-plot-demo"
private const val CALL_FUN = "jetbrains.datalore.visualization.plotDemo.component.axisComponentDemo"
private val LIBS = KOTLIN_LIBS_JS + BASE_MAPPER_LIBS_JS + PLOT_LIBS_JS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Axis component")
    }
}
