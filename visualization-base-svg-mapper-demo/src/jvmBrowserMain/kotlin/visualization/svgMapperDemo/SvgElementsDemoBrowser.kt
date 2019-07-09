package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.BASE_MAPPER_LIBS_JS
import jetbrains.datalore.visualization.base.browser.BrowserDemoUtil.KOTLIN_LIBS_JS

private const val DEMO_PROJECT = "visualization-base-svg-mapper-demo"
private const val CALL_FUN = "jetbrains.datalore.visualization.svgMapperDemo.svgElementsDemo"
private val LIBS = KOTLIN_LIBS_JS + BASE_MAPPER_LIBS_JS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "SVG - DOM mapper demo")
    }
}


