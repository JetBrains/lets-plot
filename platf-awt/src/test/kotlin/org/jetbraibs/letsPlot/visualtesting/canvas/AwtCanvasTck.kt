package org.jetbraibs.letsPlot.visualtesting.canvas

import org.jetbraibs.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.core.canvas.FontStyle.ITALIC
import org.jetbrains.letsPlot.core.canvas.FontWeight.BOLD
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.CanvasTck
import java.io.IOException
import kotlin.test.Test

typealias AwtFont = java.awt.Font

class AwtCanvasTck {

    @Test
    fun runAllCanvasClipTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = fontManager)
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        CanvasTck.runAllTests(canvasPeer, imageComparer)
    }

    companion object {
        val fontManager = FontManager().apply { 
            register("Noto Sans", createFont("fonts/NotoSans-Regular.ttf"))
            register("Noto Sans", createFont("fonts/NotoSans-Bold.ttf"), weight = BOLD)
            register("Noto Sans", createFont("fonts/NotoSans-Italic.ttf"), style = ITALIC)
            register("Noto Sans", createFont("fonts/NotoSans-BoldItalic.ttf"), weight = BOLD, style = ITALIC)
                
            register("Noto Serif", createFont("fonts/NotoSerif-Regular.ttf"))
            register("Noto Serif", createFont("fonts/NotoSerif-Bold.ttf"), weight = BOLD)
            register("Noto Serif", createFont("fonts/NotoSerif-Italic.ttf"), style = ITALIC)
            register("Noto Serif", createFont("fonts/NotoSerif-BoldItalic.ttf"), weight = BOLD, style = ITALIC)

            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Regular.ttf"))
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Regular.ttf"), style = ITALIC)
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Bold.ttf"), weight = BOLD)
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Bold.ttf"), weight = BOLD, style = ITALIC)
        }

        private fun createFont(resourceName: String): AwtFont {
            val fontStream = AwtCanvasTck::class.java.getClassLoader().getResourceAsStream(resourceName)
                    ?: error("Font resource not found: $resourceName")
            try {
                return AwtFont.createFont(AwtFont.TRUETYPE_FONT, fontStream)
                    ?: error("Cannot create font from resource: $resourceName")
            } finally {
                try {
                    fontStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
