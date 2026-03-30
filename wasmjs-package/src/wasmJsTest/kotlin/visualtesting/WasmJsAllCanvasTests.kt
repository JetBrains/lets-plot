/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package visualtesting

import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.AllCanvasTests
import kotlin.js.Promise
import kotlin.test.Ignore
import kotlin.test.Test


@Ignore
class WasmJsAllCanvasTests {

    @Test
    fun runAllCanvasTests(): Promise<JsAny?> {
        return WasmBitmapIO.preloadExpectedImages("canvas").then {
            val canvasPeer = DomCanvasPeer()
            val bitmapIO = WasmBitmapIO(subdir = "canvas")
            val imageComparer = ImageComparer(canvasPeer, bitmapIO, silent = true)
            val result = runCatching {
                AllCanvasTests.runAllTests(canvasPeer, imageComparer)
            }
            WasmBitmapIO.awaitPendingArtifactUploads().then {
                result.getOrThrow()
                null
            }
        }
    }
}
