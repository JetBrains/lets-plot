package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgColors.SvgColorKeyword
import jetbrains.datalore.visualization.base.svgToCanvas.Context2DCanvasContext.Companion.parseColorString
import jetbrains.datalore.visualization.base.svgToCanvas.Context2DCanvasContext.Companion.parseSvgColorString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Context2DCanvasContextTest {

    @Test
    fun parseColorStrings() {
        assertEquals(Color.GRAY, parseColorString("rgb(128, 128,128 )"))
        assertEquals(Color.TRANSPARENT, parseColorString("rgba( 0,0, 0, 0)"))
        assertEquals(Color.GRAY, parseColorString("#808080"))
        assertEquals(Color.GRAY, parseColorString("gray"))
        assertEquals(Color.GRAY, parseColorString("color( 128 ,128, 128)"))
        assertEquals(Color.TRANSPARENT, parseColorString("color(0, 0 ,0,0 )"))
        assertNull(parseColorString(null))
    }

    @Test
    fun parseSvgColorStrings() {
        assertEquals(Color.GRAY.toCssColor(), parseSvgColorString(Color.GRAY.toString()).toString())
        assertEquals(Color.GRAY.toCssColor(), parseSvgColorString(Color.GRAY.toHexColor()).toString())
        assertEquals(Color.GRAY.toCssColor(), parseSvgColorString(Color.GRAY.toCssColor()).toString())

        assertEquals(SvgColorKeyword.CORAL, parseSvgColorString(SvgColorKeyword.CORAL.toString()))
        assertEquals(SvgColorKeyword.NONE, parseSvgColorString(SvgColorKeyword.NONE.toString()))
        assertEquals(SvgColorKeyword.NONE, parseSvgColorString(null))
    }
}