/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.annotation.IgnoreJs
import jetbrains.datalore.vis.canvas.CssStyleUtil.extractStyleFont
import jetbrains.datalore.vis.canvas.CssStyleUtil.scaleFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


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
    @IgnoreJs
    fun scaleSimpleFont() {
        val actual = scaleFont("12px arial", TEST_SCALE)
        assertEquals("24.0px arial", actual)
    }

    @Test
    @IgnoreJs
    fun scaleFontWithSlash() {
        assertEquals("24.0px/20.0px sans-serif", scaleFont("12px/10px sans-serif",
            TEST_SCALE
        ))
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