/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil

private const val DEMO_PROJECT = "plot-demo"
private const val CALL_FUN = "jetbrains.datalore.plotDemo.component.textSizeEstimationDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(
            DEMO_PROJECT,
            CALL_FUN,
            "Text size estimation"
        )
    }
}