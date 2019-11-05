/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapperDemo

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.DEMO_COMMON_LIBS
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.KOTLIN_LIBS

private const val DEMO_PROJECT = "vis-demo-svg-mapper"
private const val CALL_FUN = "jetbrains.datalore.vis.svgMapperDemo.svgElementsDemoA"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(
            DEMO_PROJECT,
            CALL_FUN,
            LIBS, "SVG - DOM mapper demo")
    }
}


