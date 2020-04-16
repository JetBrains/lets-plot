/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.livemap.demo.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "livemap-demo"
private const val CALL_FUN = "jetbrains.livemap.demo.pointsDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(DEMO_PROJECT, CALL_FUN, "Points Demo")
    }
}
