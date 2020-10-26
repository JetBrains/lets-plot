/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil

private const val DEMO_PROJECT = "plot-demo"
private const val CALL_FUN = "jetbrains.datalore.plotDemo.plotAssembler.loessRegressionDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(
            DEMO_PROJECT,
            CALL_FUN,
            "Loess regression plot"
        )
    }
}