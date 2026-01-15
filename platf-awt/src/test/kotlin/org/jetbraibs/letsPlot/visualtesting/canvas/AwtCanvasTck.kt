package org.jetbraibs.letsPlot.visualtesting.canvas

import org.jetbraibs.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle.ITALIC
import org.jetbrains.letsPlot.core.canvas.FontWeight.BOLD
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.CanvasTck
import org.junit.BeforeClass
import java.io.IOException
import java.io.InputStream
import kotlin.test.Test

typealias AwtFont = java.awt.Font

class AwtCanvasTck {
    //@Ignore("monospace font inconsistency")
    @Test
    fun runAllCanvasClipTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = fontManager)
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        CanvasTck.runAllTests(canvasPeer, imageComparer)
    }

    companion object {
        val fontManager = FontManager(mutableMapOf<Font, AwtFont>().also {
            it[Font(fontFamily = "Noto Sans", fontSize = 1.0)] = createFont("fonts/NotoSans-Regular.ttf")
            it[Font(fontFamily = "Noto Sans", fontWeight = BOLD, fontSize = 1.0)] = createFont("fonts/NotoSans-Bold.ttf")
            it[Font(fontFamily = "Noto Sans", fontStyle = ITALIC, fontSize = 1.0)] = createFont("fonts/NotoSans-Italic.ttf")
            it[Font(fontFamily = "Noto Sans", fontWeight = BOLD, fontStyle = ITALIC, fontSize = 1.0)] =
                createFont("fonts/NotoSans-BoldItalic.ttf")

            it[Font(fontFamily = "Noto Serif", fontSize = 1.0)] = createFont("fonts/NotoSerif-Regular.ttf")
            it[Font(fontFamily = "Noto Serif", fontWeight = BOLD, fontSize = 1.0)] = createFont("fonts/NotoSerif-Bold.ttf")
            it[Font(fontFamily = "Noto Serif", fontStyle = ITALIC, fontSize = 1.0)] = createFont("fonts/NotoSerif-Italic.ttf")
            it[Font(fontFamily = "Noto Serif", fontWeight = BOLD, fontStyle = ITALIC, fontSize = 1.0)] =
                createFont("fonts/NotoSerif-BoldItalic.ttf")

            it[Font(fontFamily = "Noto Sans Mono", fontSize = 1.0)] = createFont("fonts/NotoSansMono-Regular.ttf")
            it[Font(fontFamily = "Noto Sans Mono", fontWeight = BOLD, fontSize = 1.0)] = createFont("fonts/NotoSansMono-Bold.ttf")
            it[Font(fontFamily = "Noto Sans Mono", fontWeight = BOLD, fontStyle = ITALIC, fontSize = 1.0)] = createFont("fonts/NotoSansMono-Bold.ttf")
        }
        )

        @JvmStatic
        @BeforeClass
        fun setUp() {
        }

        private fun createFont(resourceName: String): AwtFont {
            val fontStream: InputStream =
                AwtCanvasTck::class.java.getClassLoader().getResourceAsStream(resourceName)
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