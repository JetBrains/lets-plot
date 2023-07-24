/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

@Suppress("unused")
@JsName("textAlignAndBaselineDemo")
fun textAlignAndBaselineDemo() {
    demo.livemap.canvasDemo.baseCanvasDemo { canvas, _ ->
        TextAlignAndBaselineDemoModel(canvas)
    }
}