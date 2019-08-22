package jetbrains.datalore.visualization.plotDemo.plotAssembler

import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil

private const val DEMO_PROJECT = "visualization-demo-plot"
private const val CALL_FUN = "jetbrains.datalore.visualization.plotDemo.plotAssembler.linearRegressionDemo"
private val LIBS =
    BrowserDemoUtil.KOTLIN_LIBS + BrowserDemoUtil.BASE_MAPPER_LIBS + BrowserDemoUtil.PLOT_LIBS + BrowserDemoUtil.DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Linear regression plot")
    }
}
