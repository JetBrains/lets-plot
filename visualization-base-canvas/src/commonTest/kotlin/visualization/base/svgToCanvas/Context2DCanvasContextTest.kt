package visualization.base.svgToCanvas

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgColors.CORAL
import jetbrains.datalore.visualization.base.svgToCanvas.Context2DCanvasContext.Companion.parseColorString
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class Context2DCanvasContextTest {
    @Ignore
    @Test
    fun parseColorString() {
        assertEquals(Color.GRAY, parseColorString("rgb(128, 128,128 )"))
        assertEquals(Color.TRANSPARENT, parseColorString("rgba( 0,0, 0, 0)"))
        assertEquals(Color.GRAY, parseColorString("#808080"))
        assertEquals(Color.GRAY, parseColorString("gray"))

        val coral: Color = Color(255, 127, 80)
        assertEquals(coral, parseColorString(CORAL.toString()))
    }
}