/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.KOTLIN_LIBS
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.PLOT_LIBS
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "plot-demo"
private const val CALL_FUN = "jetbrains.datalore.plotDemo.component.axisComponentDemo"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + PLOT_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(
            DEMO_PROJECT,
            CALL_FUN,
            LIBS, "Axis component"
        )
    }
}
