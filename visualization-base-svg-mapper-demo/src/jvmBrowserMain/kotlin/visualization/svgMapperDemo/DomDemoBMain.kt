package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.DEMO_COMMON_LIBS
import jetbrains.datalore.visualization.demoUtils.browser.BrowserDemoUtil.KOTLIN_LIBS

private const val DEMO_PROJECT = "visualization-base-svg-mapper-demo"
private const val CALL_FUN = "jetbrains.datalore.visualization.svgMapperDemo.svgElementsDemo"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "SVG - DOM mapper demo")
    }
}


