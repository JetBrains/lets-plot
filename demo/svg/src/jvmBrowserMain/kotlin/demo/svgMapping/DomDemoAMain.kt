/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import demo.common.util.demoUtils.browser.BrowserDemoUtil

private const val DEMO_PROJECT_PATH = "demo/svg"
private const val DEMO_PROJECT = "demo-svg"
private const val CALL_FUN = "demo.svgMapping.svgElementsDemoA"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT_PATH) {
        BrowserDemoUtil.mapperDemoHtml(
            DEMO_PROJECT_PATH,
            DEMO_PROJECT,
            CALL_FUN,
            "SVG - DOM mapper demo"
        )
    }
}


