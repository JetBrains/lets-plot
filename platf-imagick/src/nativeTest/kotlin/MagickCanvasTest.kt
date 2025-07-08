import org.jetbrains.letsPlot.core.canvas.Font
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class MagickCanvasTest {
    @Test
    fun unknownFont() {
        val (canvas, ctx) = createCanvas()
        ctx.setFont(Font(fontFamily = "monospace", fontSize = 28.0))
        ctx.fillText("Hello,", 0.0, 20.0)
        ctx.setFont(Font(fontFamily = "Times New Roman", fontSize = 28.0))
        ctx.fillText("World!", 0.0, 48.0)

        // No assertion needed; the test passes if no exception is thrown.
        assertCanvas("text_unknown_font.png", canvas)
    }
}
