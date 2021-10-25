/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("textStyleDemo")
@JsExport
fun textStyleDemo() {
    baseCanvasDemo { canvas, _ ->
        TextStyleDemoModel(canvas)
    }
}