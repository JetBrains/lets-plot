/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("drawImageDemo")
@JsExport
fun drawImageDemo() {
    demo.livemap.canvasDemo.baseCanvasDemo { canvas, createSnapshot ->
        demo.livemap.canvasDemo.DrawImageAndClearRectDemoModel(canvas, createSnapshot)
    }
}