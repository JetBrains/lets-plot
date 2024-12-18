/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.component

import demo.livemap.LiveMapBrowserDemoUtil
import demo.livemap.LiveMapBrowserDemoUtil.mapperDemoHtml


// To run:
// ./gradlew :js-package:build
// ./gradlew :demo-livemap-browser:build
// ./gradlew :demo-livemap-browser:jsBrowserProductionWebpack

fun runComponentDemo(name: String, title: String) {
    val callFun = "demo.livemap.component.$name"
    LiveMapBrowserDemoUtil.openInBrowser {
        mapperDemoHtml(callFun, title)
    }
}