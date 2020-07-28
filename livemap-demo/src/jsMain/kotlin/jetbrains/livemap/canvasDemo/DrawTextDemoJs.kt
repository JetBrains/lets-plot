/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.js.dom.DomContext2d

@Suppress("unused")
@JsName("drawTextDemo")
fun drawTextDemo() {
    baseCanvasDemo { canvas, _ ->
        DrawTextDemoModel(canvas)
    }
}