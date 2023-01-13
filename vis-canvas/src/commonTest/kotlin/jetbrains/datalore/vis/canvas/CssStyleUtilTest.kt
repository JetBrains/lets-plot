/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.vis.canvas.CssStyleUtil.extractStyleFont
import jetbrains.datalore.vis.canvas.CssStyleUtil.scaleFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CssStyleUtilTest {

    @Test
    fun getFontFromStyle() {
        val actual = extractStyleFont("font: normal small-caps 12px/14px fantasy;")
        assertEquals("normal small-caps 12px/14px fantasy", actual)
    }

    @Test
    fun getEmptyFontFromStyleWithoutFont() {
        val actual = extractStyleFont("fill:#3d3d3d;")
        assertNull(actual)
    }

    @Test
    fun scaleSimpleFont() {
        val commonExpected = "24.0px arial"
        val jsExpected = "24px arial"
        val actual = scaleFont("12px arial", TEST_SCALE)
        assertTrue((commonExpected == actual) || (jsExpected == actual))
    }

    @Test
    fun scaleFontWithSlash() {
        val commonExpected = "24.0px/20.0px sans-serif"
        val jsExpected = "24px/20px sans-serif"
        val actual = scaleFont("12px/10px sans-serif", TEST_SCALE)
        assertTrue((commonExpected == actual) || (jsExpected == actual))
    }

    @Test
    fun scaleFontWithPercent() {
        assertEquals("bold italic 110% serif", scaleFont("bold italic 110% serif",
            TEST_SCALE
        ))
    }

    companion object {
        private const val TEST_SCALE = 2.0
    }
}