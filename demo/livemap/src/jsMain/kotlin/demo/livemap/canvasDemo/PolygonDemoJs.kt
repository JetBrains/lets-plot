/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("polygonDemo")
@JsExport
fun polygonDemo() {
    demo.livemap.canvasDemo.baseCanvasDemo { canvas, _ ->
        PolygonDemoModel(canvas)
    }
}