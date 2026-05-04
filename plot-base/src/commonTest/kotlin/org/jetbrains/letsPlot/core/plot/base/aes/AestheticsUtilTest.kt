/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import kotlin.test.Test
import kotlin.test.assertEquals

class AestheticsUtilTest {

    private fun point(
        color: Color = Color.RED,
        fill: Color = Color.BLUE,
        alpha: Double? = null,
        segmentAlpha: Double? = null
    ): DataPointAesthetics {
        val builder = AestheticsBuilder(1)
            .color(AestheticsBuilder.constant(color))
            .fill(AestheticsBuilder.constant(fill))
        if (alpha != null) {
            builder.alpha(AestheticsBuilder.constant(alpha))
        }
        if (segmentAlpha != null) {
            builder.segmentAlpha(AestheticsBuilder.constant(segmentAlpha))
        }
        return builder.build().dataPoints().first()
    }

    // --- resolveColor() / resolveFill() ---

    @Test
    fun `resolveFill with explicit fill no explicit alpha leaves color unchanged`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveFill(point(fill = color), color)
        assertEquals(color, resolved)
    }

    @Test
    fun `resolveFill with default alpha sentinel leaves color alpha unchanged`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveFill(point(fill = color, alpha = AesInitValue.DEFAULT_ALPHA), color)
        assertEquals(128, resolved.alpha)
    }

    @Test
    fun `resolveFill with explicit fill explicit alpha replaces color alpha`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveFill(point(fill = color, alpha = 0.25), color)
        assertEquals(64, resolved.alpha)
    }

    @Test
    fun `resolveFill with explicit zero alpha makes color transparent`() {
        val color = Color(255, 0, 0, 128)
        val resolved = AestheticsUtil.resolveFill(point(fill = color, alpha = 0.0), color)
        assertEquals(0, resolved.alpha)
    }

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

    @Test
    fun `resolveFill applies explicit alpha to fill color`() {
        val fill = Color(0, 0, 255, 128)
        val resolved = AestheticsUtil.resolveFill(point(fill = fill, alpha = 0.25))
        assertEquals(64, resolved.alpha)
        assertEquals(fill.red, resolved.red)
        assertEquals(fill.green, resolved.green)
        assertEquals(fill.blue, resolved.blue)
    }

    @Test
    fun `effectiveSegmentAlpha uses alpha when segment alpha is not explicitly set`() {
        assertEquals(0.25, AestheticsUtil.effectiveSegmentAlpha(point(alpha = 0.25)))
    }

    @Test
    fun `effectiveSegmentAlpha uses explicit segment alpha`() {
        assertEquals(0.5, AestheticsUtil.effectiveSegmentAlpha(point(alpha = 0.25, segmentAlpha = 0.5)))
    }
}
