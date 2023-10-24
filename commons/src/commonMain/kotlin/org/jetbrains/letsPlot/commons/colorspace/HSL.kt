/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.abs
import kotlin.math.roundToInt

/*
 * Hue, Saturation, Lightness
 *  h: 0..360
 *  s: 0..1
 *  l: 0..1
 */
data class HSL(
    val h: Double,
    val s: Double,
    val l: Double,
)


fun hslFromRgb(rgb: Color): HSL {
    // see https://www.rapidtables.com/convert/color/rgb-to-hsl.html for details
    val r = rgb.red / 255.0
    val g = rgb.green / 255.0
    val b = rgb.blue / 255.0

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val delta = max - min

    val l = (max + min) / 2.0

    val s = when (delta) {
        0.0 -> 0.0
        else -> delta / (1.0 - abs(2.0 * l - 1.0))
    }

    val h = when (delta) {
        0.0 -> 0.0
        else -> when (max) {
            r -> (g - b) / (max - min) % 6
            g -> (b - r) / (max - min) + 2
            b -> (r - g) / (max - min) + 4
            else -> error("max value ($max) does not match any of r($r), g($g), b($b)")
        }
    } * 60.0

    return HSL(
        h = if (h >= 0.0) h else h + 360.0,
        s = s,
        l = l
    )
}


fun rgbFromHsl(hsl: HSL, alpha: Double = 1.0): Color {
    val c = (1.0 - abs(2 * hsl.l - 1.0)) * hsl.s
    val h2 = hsl.h / 60
    val x = c * (1 - abs(h2 % 2 - 1))
    val (r1, g1, b1) = when (h2) {
        in 0.0..1.0 -> Triple(c, x, 0.0)
        in 1.0..2.0 -> Triple(x, c, 0.0)
        in 2.0..3.0 -> Triple(0.0, c, x)
        in 3.0..4.0 -> Triple(0.0, x, c)
        in 4.0..5.0 -> Triple(x, 0.0, c)
        in 5.0..6.0 -> Triple(c, 0.0, x)
        else -> error("Unexpected h2 value: $h2")
    }
    val m = hsl.l - c / 2.0
    return Color(
        ((r1 + m) * 255).roundToInt(),
        ((g1 + m) * 255).roundToInt(),
        ((b1 + m) * 255).roundToInt(),
        (255 * 1.0).roundToInt()
    ).changeAlpha((255 * alpha).roundToInt())
}
