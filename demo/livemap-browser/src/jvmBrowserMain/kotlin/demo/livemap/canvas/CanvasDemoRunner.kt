/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas

import demo.livemap.LiveMapBrowserDemoUtil

fun runCanvasDemo(name: String, title: String) {
    val callFun = "demo.livemap.canvas.$name"
    LiveMapBrowserDemoUtil.openInBrowser {
        LiveMapBrowserDemoUtil.mapperDemoHtml(callFun, title)
    }
}