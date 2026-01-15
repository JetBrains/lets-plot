import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager.FontSet
import kotlin.math.PI
import kotlin.test.Test

class CanvasTextTest : CanvasTestBase() {
    companion object {
        val monoRegularFontManager by lazy {
            MagickFontManager.configured(
                "mono" to FontSet(
                    embedded = true,
                    "NotoMono",
                    regularFontPath = notoSansMonoRegularFontPath
                ),
            )
        }

        private val monoBoldFontManager by lazy {
            MagickFontManager.configured(
                "mono" to FontSet(
                    embedded = true,
                    "NotoMono",
                    regularFontPath = notoSansMonoRegularFontPath,
                    boldFontPath = notoSansMonoBoldFontPath
                )
            )
        }

        private val serifRegularOnlyFontManager by lazy {
            MagickFontManager.configured(
                "serif" to FontSet(embedded = true, "NotoSerif", regularFontPath = notoSerifRegularFontPath)
            )
        }

        private val serifItalicFontManager by lazy {
            MagickFontManager.configured(
                "serif" to FontSet(
                    embedded = true,
                    "NotoSerif",
                    regularFontPath = notoSerifRegularFontPath,
                    italicFontPath = notoSerifItalicFontPath
                )
            )
        }

        private val serifBoldFontManager by lazy {
            MagickFontManager.configured(
                "serif" to FontSet(
                    embedded = true,
                    "NotoSerif",
                    regularFontPath = notoSerifRegularFontPath,
                    boldFontPath = notoSerifBoldFontPath
                )
            )
        }

        private val serifFullFontManager by lazy {
            MagickFontManager.configured(
                "serif" to FontSet(
                    embedded = true,
                    "NotoSerif",
                    regularFontPath = notoSerifRegularFontPath,
                    boldFontPath = notoSerifBoldFontPath,
                    italicFontPath = notoSerifItalicFontPath,
                    boldItalicFontPath = notoSerifBoldItalicFontPath
                )
            )
        }
    }

    @Test
    fun monospaceRealBold() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = monoBoldFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "mono", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "mono", size = fontSize, weight = FontWeight.BOLD)
        ctx.fillText("Bold", 5.0, 50.0)

        assertCanvas("monospace_bold_real.png", canvas)
    }

    @Test
    fun monospaceRealBoldFauxItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = monoBoldFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "mono", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "mono", size = fontSize, style = FontStyle.ITALIC, weight = FontWeight.BOLD)
        ctx.fillText("BoldItalic", 5.0, 50.0)

        assertCanvas("monospace_bold_italic_real_faux.png", canvas)
    }

    @Test
    fun serifRegular() {
        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = 16.0)
        ctx.fillText("Regular", 5.0, 30.0)

        assertCanvas("serif_regular.png", canvas)
    }

    @Test
    fun serifRealItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifItalicFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC)
        ctx.fillText("Italic", 5.0, 50.0)

        assertCanvas("serif_italic_real.png", canvas)
    }

    @Test
    fun serifFauxItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC)
        ctx.fillText("ItalicXY", 5.0, 50.0)

        ctx.translate(5.0, 70.0)
        ctx.fillText("ItalicTR", 0.0, 0.0)

        assertCanvas("serif_italic_faux.png", canvas)
    }

    @Test
    fun serifRealBold() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifBoldFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, weight = FontWeight.BOLD)
        ctx.fillText("Bold", 5.0, 50.0)

        assertCanvas("serif_bold_real.png", canvas)
    }

    @Test
    fun serifFauxBold() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, weight = FontWeight.BOLD)
        ctx.fillText("Bold", 5.0, 50.0)

        assertCanvas("serif_bold_faux.png", canvas)
    }


    @Test
    fun serifRealBoldItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifFullFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC, weight = FontWeight.BOLD)
        ctx.fillText("BoldItalic", 5.0, 50.0)

        assertCanvas("serif_bold_italic_real_real.png", canvas)
    }

    @Test
    fun serifFauxBoldRealItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifItalicFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC, weight = FontWeight.BOLD)
        ctx.fillText("BoldItalic", 5.0, 50.0)

        assertCanvas("serif_bold_italic_faux_real.png", canvas)
    }

    @Test
    fun serifRealBoldFauxItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifBoldFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC, weight = FontWeight.BOLD)
        ctx.fillText("BoldItalic", 5.0, 50.0)

        assertCanvas("serif_bold_italic_real_faux.png", canvas)
    }

    @Test
    fun serifFauxBoldItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = fontSize)
        ctx.fillText("Regular", 5.0, 30.0)

        ctx.setFont(family = "serif", size = fontSize, style = FontStyle.ITALIC, weight = FontWeight.BOLD)
        ctx.fillText("BoldItalic", 5.0, 50.0)

        assertCanvas("serif_bold_italic_faux_faux.png", canvas)
    }

    @Test
    fun serifFauxItalicSaveRestore() {
        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = 12.0)
        ctx.save()

        ctx.setFont(family = "serif", size = 16.0, style = FontStyle.ITALIC)
        ctx.save()

        ctx.setFont(family = "serif", size = 8.0)

        // regular 8.0
        ctx.fillText("Regular 8px", 5.0, 10.0)
        ctx.restore()

        // italic 16.0
        ctx.fillText("Italic 16px", 5.0, 30.0)

        ctx.restore()

        // regular 12.0
        ctx.fillText("Regular 12px", 5.0, 50.0)

        assertCanvas("serif_italic_faux_save_restore.png", canvas)
    }

    @Test
    fun serifFauxItalicRotateXY() {
        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.rotate(PI / 4) // Rotate by 45 degrees

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = 26.0)
        ctx.fillText("Italic", 50.0, 10.0)

        ctx.fillStyle = "red"
        ctx.setFont(family = "serif", size = 26.0, style = FontStyle.ITALIC)
        ctx.fillText("Italic", 50.0, 10.0)

        assertCanvas("serif_italic_faux_rotated_xy.png", canvas)
    }

    @Test
    fun serifFauxItalicTranslateRotate() {
        val (canvas, ctx) = createCanvas(fontManager = serifRegularOnlyFontManager)

        ctx.translate(25.0, 25.0)
        ctx.rotate(PI / 4) // Rotate by 45 degrees

        ctx.fillStyle = "black"
        ctx.setFont(family = "serif", size = 26.0)
        ctx.fillText("Italic", 0.0, 0.0)

        ctx.fillStyle = "red"
        ctx.setFont(family = "serif", size = 26.0, style = FontStyle.ITALIC)
        ctx.fillText("Italic", 0.0, 0.0)

        assertCanvas("serif_italic_faux_translate_rotate.png", canvas)
    }
}