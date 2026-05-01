/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue
import kotlin.test.Test
import kotlin.test.assertEquals

class HintColorUtilTest {

    @Test
    fun `DEFAULT_ALPHA leaves the color unchanged`() {
        val color = Color(255, 0, 0, 200)
        assertEquals(color, HintColorUtil.applyAlpha(color, AesInitValue.DEFAULT_ALPHA))
    }

    @Test
    fun `explicit alpha 0_5 is applied to the color`() {
        val result = HintColorUtil.applyAlpha(Color.RED, 0.5)
        assertEquals(128, result.alpha)
        assertEquals(Color.RED.red, result.red)
        assertEquals(Color.RED.green, result.green)
        assertEquals(Color.RED.blue, result.blue)
    }

    @Test
    fun `explicit alpha 0_0 makes the color fully transparent`() {
        assertEquals(0, HintColorUtil.applyAlpha(Color.RED, 0.0).alpha)
    }

    @Test
    fun `explicit alpha 1_0 makes the color fully opaque`() {
        val color = Color(255, 0, 0, 50)
        assertEquals(255, HintColorUtil.applyAlpha(color, 1.0).alpha)
    }

    @Test
    fun `DEFAULT_ALPHA preserves existing color alpha rather than overwriting it`() {
        // Key distinction: DEFAULT_ALPHA (0.999887) would give intAlpha=254 if applied,
        // but it must be treated as "no alpha set" and leave the color's own alpha intact.
        val color = Color(255, 0, 0, 200)
        val result = HintColorUtil.applyAlpha(color, AesInitValue.DEFAULT_ALPHA)
        assertEquals(200, result.alpha)
    }
}
