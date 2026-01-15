package org.jetbraibs.letsPlot.visualtesting.canvas

import org.jetbraibs.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.CanvasTck
import org.junit.BeforeClass
import org.junit.Ignore
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.io.InputStream
import kotlin.test.Test

class AwtCanvasTck {
    @Ignore("monospace font inconsistency")
    @Test
    fun runAllCanvasClipTests() {
        val canvasPeer = AwtCanvasPeer()
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        CanvasTck.runAllTests(canvasPeer, imageComparer)
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            registerFont("fonts/NotoSans-Regular.ttf")
            registerFont("fonts/NotoSans-Bold.ttf")
            registerFont("fonts/NotoSans-Italic.ttf")
            registerFont("fonts/NotoSans-BoldItalic.ttf")
            registerFont("fonts/NotoSerif-Regular.ttf")
            registerFont("fonts/NotoSerif-Bold.ttf")
            registerFont("fonts/NotoSerif-Italic.ttf")
            registerFont("fonts/NotoSerif-BoldItalic.ttf")
            registerFont("fonts/NotoSansMono-Regular.ttf")

            // We don't use bold monospace to check how missing fonts are handled.
            //registerFont("fonts/NotoSansMono-Bold.ttf")
        }

        private fun registerFont(resourceName: String) {
            val fontStream: InputStream = AwtCanvasTck::class.java.getClassLoader().getResourceAsStream(resourceName) ?: error("Font resource not found: $resourceName")
            try {
                val customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
                ge.registerFont(customFont)
            } catch (e: FontFormatException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
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