/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil
import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "plot-demo"
private const val CALL_FUN = "jetbrains.datalore.plotDemo.component.axisComponentDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(
            "ToDo-ProjPath",  // ToDo
            DEMO_PROJECT,
            CALL_FUN,
            "Axis component"
        )
    }
}
