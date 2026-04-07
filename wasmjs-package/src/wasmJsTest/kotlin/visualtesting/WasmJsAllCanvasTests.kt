/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package visualtesting

import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasPeer
import org.jetbrains.letsPlot.platf.w3c.canvas.DomFontManager
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.AllCanvasTests
import kotlin.js.Promise
import kotlin.test.Test

@JsFun(
    """
    (relativePath) => {
        const files = Object.keys(globalThis.__karma__?.files ?? {});
        return files.find((path) => path.endsWith('/' + relativePath) || path.endsWith(relativePath))
            || ('/base/' + relativePath);
    }
    """
)
private external fun resolveTestResourceUrl(relativePath: String): String

@JsFun("() => document.fonts.ready")
private external fun documentFontsReady(): Promise<JsAny?>

//@Ignore
class WasmJsAllCanvasTests {
    @Test
    fun runAllCanvasTests(): Promise<JsAny?> {
        val fontManager = createEmbeddedFontsManager()
        fontManager.installAllFontFaces()

        return documentFontsReady().then {
            WasmBitmapIO.preloadExpectedImages("canvas").then {
                val canvasPeer = DomCanvasPeer(fontManager)
                val bitmapIO = WasmBitmapIO(subdir = "canvas")
                val imageComparer = ImageComparer(
                    canvasPeer,
                    bitmapIO,
                    profileAdjuster = { context ->
                        context.profile.withBrowserAaTolerance()
                    },
                    silent = true
                )
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

    private fun createEmbeddedFontsManager(): DomFontManager {
        fun fontUrl(fileName: String): String {
            return resolveTestResourceUrl("wasmjs-package/src/wasmJsTest/resources/fonts/$fileName")
        }

        val notoSans = DomFontManager.FontSet(
            familyName = "Noto Sans",
            regularFontPath = fontUrl("NotoSans-Regular.ttf"),
            boldFontPath = fontUrl("NotoSans-Bold.ttf"),
            italicFontPath = fontUrl("NotoSans-Italic.ttf"),
            boldItalicFontPath = fontUrl("NotoSans-BoldItalic.ttf")
        )
        val notoSerif = DomFontManager.FontSet(
            familyName = "Noto Serif",
            regularFontPath = fontUrl("NotoSerif-Regular.ttf"),
            boldFontPath = fontUrl("NotoSerif-Bold.ttf"),
            italicFontPath = fontUrl("NotoSerif-Italic.ttf"),
            boldItalicFontPath = fontUrl("NotoSerif-BoldItalic.ttf")
        )
        val notoMono = DomFontManager.FontSet(
            familyName = "Noto Sans Mono",
            regularFontPath = fontUrl("NotoSansMono-Regular.ttf"),
            boldFontPath = fontUrl("NotoSansMono-Bold.ttf")
        )

        return DomFontManager.configured(
            "Noto Sans" to notoSans,
            "Noto Serif" to notoSerif,
            "Noto Sans Mono" to notoMono,
            "sans" to notoSans,
            "sans-serif" to notoSans,
            "serif" to notoSerif,
            "mono" to notoMono,
            "regular_mono" to notoMono,
            "oblique" to notoSans,
            "oblique_bold" to notoSans
        )
    }
}
