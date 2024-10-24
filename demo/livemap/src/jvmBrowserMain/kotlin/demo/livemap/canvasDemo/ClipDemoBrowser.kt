/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

import demo.livemap.demo.BrowserDemoUtil

private const val DEMO_PROJECT = "demo-livemap"
private const val CALL_FUN = "demo.livemap.canvasDemo.clipDemo"

fun main() {
    BrowserDemoUtil.openInBrowser {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, "drawImage() and clearRect() Demo")
    }
}