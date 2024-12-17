/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import demo.livemap.LiveMapBrowserDemoUtil
import demo.livemap.LiveMapBrowserDemoUtil.mapperDemoHtml


private const val CALL_FUN = "demo.livemap.demo.pointsDemo"

fun main() {
    LiveMapBrowserDemoUtil.openInBrowser {
        mapperDemoHtml(CALL_FUN, "Points Demo")
    }
}
