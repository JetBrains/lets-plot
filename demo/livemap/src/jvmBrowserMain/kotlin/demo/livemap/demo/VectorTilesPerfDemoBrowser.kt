/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

private const val DEMO_PROJECT = "demo-livemap"
private const val CALL_FUN = "demo.livemap.demo.vectorTilesPerfDemo"

fun main() {
    BrowserDemoUtil.openInBrowser {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, "Vector Tiles Perf Demo")
    }
}