/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.style

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StyleSheetTest {

    private fun sheetWith(color: Color): StyleSheet {
        return StyleSheet(
            mapOf("cls" to TextStyle("Arial", FontFace.NORMAL, 12.0, color)),
            defaultFamily = "Arial"
        )
    }

    // --- toCSS ---

    @Test
    fun `opaque color does not emit fill-opacity`() {
        val css = sheetWith(Color.RED).toCSS()
        assertTrue(css.contains("fill: #ff0000"))
        assertFalse(css.contains("fill-opacity"))
    }

    @Test
    fun `semi-transparent color emits fill and fill-opacity separately`() {
        val css = sheetWith(Color(255, 0, 0, 128)).toCSS()
        assertTrue(css.contains("fill: #ff0000"))
        assertTrue(css.contains("fill-opacity:"))
    }

    @Test
    fun `fully transparent color emits fill-opacity of 0`() {
        val css = sheetWith(Color(0, 255, 0, 0)).toCSS()
        assertTrue(css.contains("fill-opacity: 0"))
    }

    // --- fromCSS roundtrip ---

    @Test
    fun `semi-transparent color alpha survives CSS roundtrip`() {
        val original = Color(255, 0, 0, 128)
        val css = sheetWith(original).toCSS()
        val parsed = StyleSheet.fromCSS(css, defaultFamily = "Arial", defaultSize = 12.0)
        val color = parsed.getTextStyle("cls").color
        assertEquals(original.red, color.red)
        assertEquals(original.green, color.green)
        assertEquals(original.blue, color.blue)
        assertEquals(original.alpha, color.alpha)
    }

    @Test
    fun `fully transparent alpha survives CSS roundtrip`() {
        val css = sheetWith(Color(0, 255, 0, 0)).toCSS()
        val parsed = StyleSheet.fromCSS(css, defaultFamily = "Arial", defaultSize = 12.0)
        assertEquals(0, parsed.getTextStyle("cls").color.alpha)
    }

    @Test
    fun `opaque color alpha survives CSS roundtrip`() {
        val css = sheetWith(Color.BLUE).toCSS()
        val parsed = StyleSheet.fromCSS(css, defaultFamily = "Arial", defaultSize = 12.0)
        assertEquals(255, parsed.getTextStyle("cls").color.alpha)
    }

    @Test
    fun `color alpha and fill-opacity are composed`() {
        val css = """
            .cls {
                fill: #ff000080;
                fill-opacity: 0.5;
            }
        """.trimIndent()
        val parsed = StyleSheet.fromCSS(css, defaultFamily = "Arial", defaultSize = 12.0)
        assertEquals(64, parsed.getTextStyle("cls").color.alpha)
    }
}
