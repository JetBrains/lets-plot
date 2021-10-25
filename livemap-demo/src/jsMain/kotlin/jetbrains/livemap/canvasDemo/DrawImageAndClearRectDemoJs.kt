/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("drawImageDemo")
@JsExport
fun drawImageDemo() {
    baseCanvasDemo { canvas, createSnapshot ->
        DrawImageAndClearRectDemoModel(canvas, createSnapshot)
    }
}