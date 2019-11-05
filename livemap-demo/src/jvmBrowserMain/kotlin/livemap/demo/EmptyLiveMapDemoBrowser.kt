/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.livemap.demo.BrowserDemoUtil.BASE_MAPPER_LIBS
import jetbrains.livemap.demo.BrowserDemoUtil.DEMO_COMMON_LIBS
import jetbrains.livemap.demo.BrowserDemoUtil.KOTLIN_LIBS
import jetbrains.livemap.demo.BrowserDemoUtil.PLOT_LIBS
import jetbrains.livemap.demo.BrowserDemoUtil.mapperDemoHtml

private const val DEMO_PROJECT = "livemap-demo"
private const val CALL_FUN = "jetbrains.livemap.demo.emptyLiveMapDemo"
private val LIBS = KOTLIN_LIBS + BASE_MAPPER_LIBS + PLOT_LIBS + DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Simple map Demo")
    }
}
