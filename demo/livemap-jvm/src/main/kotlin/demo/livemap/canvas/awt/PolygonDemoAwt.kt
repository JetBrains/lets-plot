/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas.awt

import demo.livemap.common.canvas.PolygonDemoModel

class PolygonDemoAwt {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            baseCanvasDemo { canvas, _ ->
                PolygonDemoModel(canvas)
            }
        }
    }
}