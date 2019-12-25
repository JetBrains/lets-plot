/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

private const val DEMO_PROJECT = "livemap-demo"
private const val CALL_FUN = "jetbrains.livemap.demo.textDemo"
private val LIBS = BrowserDemoUtil.KOTLIN_LIBS + BrowserDemoUtil.BASE_MAPPER_LIBS + BrowserDemoUtil.PLOT_LIBS + BrowserDemoUtil.DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Text Demo")
    }
}