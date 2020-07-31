/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.livemap.demo.BrowserDemoUtil

private const val DEMO_PROJECT = "livemap-demo"
private const val CALL_FUN = "jetbrains.livemap.canvasDemo.textAlignAndBaselineDemo"

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, "Text align and baseline Demo")
    }
}