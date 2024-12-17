/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

import demo.livemap.LiveMapBrowserDemoUtil


private const val CALL_FUN = "demo.livemap.canvasDemo.pathDemo"

fun main() {
    LiveMapBrowserDemoUtil.openInBrowser {
        LiveMapBrowserDemoUtil.mapperDemoHtml(CALL_FUN, "Path Demo")
    }
}