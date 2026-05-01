/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AestheticsUtilTest {

    private fun point(
        color: Color = Color.RED,
        fill: Color = Color.BLUE,
        alpha: Double? = null
    ): DataPointAesthetics {
        val builder = AestheticsBuilder(1)
            .color(AestheticsBuilder.constant(color))
            .fill(AestheticsBuilder.constant(fill))
        if (alpha != null) {
            builder.alpha(AestheticsBuilder.constant(alpha))
        }
        return builder.build().dataPoints().first()
    }

    // --- isExplicitAlphaValue ---

    @Test
    fun `null is not an explicit alpha`() {
        assertFalse(AestheticsUtil.isExplicitAlphaValue(null))
    }

    @Test
    fun `DEFAULT_ALPHA sentinel is not an explicit alpha`() {
        assertFalse(AestheticsUtil.isExplicitAlphaValue(AesInitValue.DEFAULT_ALPHA))
    }

    @Test
    fun `0_5 is an explicit alpha`() {
        assertTrue(AestheticsUtil.isExplicitAlphaValue(0.5))
    }

    @Test
    fun `0_0 is an explicit alpha`() {
        assertTrue(AestheticsUtil.isExplicitAlphaValue(0.0))
    }

    // --- alpha() ---

    @Test
    fun `no aesthetic alpha falls back to the color's own alpha`() {
        val color = Color(255, 0, 0, 128)
        val p = point(color = color)   // no explicit alpha set
        assertEquals(SvgUtils.alpha2opacity(128), AestheticsUtil.alpha(color, p))
    }

    @Test
    fun `explicit aesthetic alpha overrides the color's alpha`() {
        val color = Color(255, 0, 0, 128)   // color has its own alpha
        val p = point(color = color, alpha = 0.25)
        assertEquals(0.25, AestheticsUtil.alpha(color, p))
    }

    // --- resolveColor() ---

    @Test
    fun `resolveColor applyAlpha=true no explicit alpha - alpha comes from color`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveColor(point(color = color), applyAlpha = true)
        assertEquals(128, resolved.alpha)
    }

    @Test
    fun `resolveColor applyAlpha=true explicit alpha - alpha comes from aesthetic`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveColor(point(color = color, alpha = 0.25), applyAlpha = true)
        assertEquals(64, resolved.alpha)
    }

    @Test
    fun `resolveColor applyAlpha=false always uses color's alpha regardless of aesthetic`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveColor(point(color = color, alpha = 0.25), applyAlpha = false)
        assertEquals(128, resolved.alpha)
    }
}
