/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.component

import demo.common.utils.browser.BrowserDemoUtil
import demo.common.utils.browser.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT_PATH = "demo/plot"
private const val DEMO_PROJECT = "demo-plot"
private const val CALL_FUN = "demo.plot.js.component.axisComponentDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
        mapperDemoHtml(
            demoProjectPath = DEMO_PROJECT_PATH,
            DEMO_PROJECT,
            CALL_FUN,
            "Axis component"
        )
    }
}
