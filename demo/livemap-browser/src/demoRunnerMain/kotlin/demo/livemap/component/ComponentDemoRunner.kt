/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.component

import demo.common.utils.browser.BrowserDemoUtil
import demo.livemap.LiveMapBrowserDemoUtil


// To run:
// ./gradlew :js-package:build
// ./gradlew :demo-livemap-browser:build
// ./gradlew :demo-livemap-browser:jsBrowserProductionWebpack

fun runComponentDemo(name: String, title: String) {
    val callFun = "demo.livemap.component.$name"
    BrowserDemoUtil.openInBrowserJs(LiveMapBrowserDemoUtil.DEMO_PROJECT_PATH) {
        BrowserDemoUtil.mapperDemoHtml(
            demoProjectPath = LiveMapBrowserDemoUtil.DEMO_PROJECT_PATH,
            demoProject = LiveMapBrowserDemoUtil.DEMO_PROJECT,
            callFun = callFun,
            title = title,
            projectDeps = emptyList(),
            target = BrowserDemoUtil.Target.JS
        )
    }
}