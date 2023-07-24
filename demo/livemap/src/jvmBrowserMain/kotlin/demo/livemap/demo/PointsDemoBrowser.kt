/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import demo.livemap.demo.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "demo-livemap"
private const val CALL_FUN = "demo.livemap.demo.pointsDemo"

fun main() {
    BrowserDemoUtil.openInBrowser {
        mapperDemoHtml(DEMO_PROJECT, CALL_FUN, "Points Demo")
    }
}
