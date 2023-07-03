/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

@Suppress("unused")
@JsName("perfDemo")
fun perfDemo() {
    DemoBaseJs(::PerfDemoModel).show()
}