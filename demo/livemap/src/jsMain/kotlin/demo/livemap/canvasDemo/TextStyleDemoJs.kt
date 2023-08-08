/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

@JsExport
fun textStyleDemo() {
    demo.livemap.canvasDemo.baseCanvasDemo { canvas, _ ->
        TextStyleDemoModel(canvas)
    }
}
