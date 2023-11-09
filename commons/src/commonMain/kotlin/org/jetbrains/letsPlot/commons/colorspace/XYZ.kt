/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.pow
import kotlin.math.roundToInt


internal const val referenceX = 0.95047 * 100.0
internal const val referenceY = 1.00000 * 100.0
internal const val referenceZ = 1.08883 * 100.0

internal const val cieE = 0.008856

/**
 * CIE XYZ
 *  x: 0..100 (may exceed limit)
 *  y: 0..100 (may exceed limit)
 *  z: 0..100 (may exceed limit)
 */
data class XYZ(
    val x: Double,
    val y: Double,
    val z: Double,
)

/**
 * References:
 * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_RGB.html
 * https://www.easyrgb.com/en/math.php
 */
fun rgbFromXyz(xyz: XYZ): Color {
    fun transform(v: Double): Double = when {
        v > 0.0031308 -> 1.055 * (v.pow(1 / 2.4)) - 0.055
        else -> 12.92 * v
    }

    val x = xyz.x / 100.0
    val y = xyz.y / 100.0
    val z = xyz.z / 100.0

    val xyzR = x * 3.2406 + y * -1.5372 + z * -0.4986
    val xyzG = x * -0.9689 + y * 1.8758 + z * 0.0415
    val xyzB = x * 0.0557 + y * -0.2040 + z * 1.0570

    val r = transform(xyzR)
    val g = transform(xyzG)
    val b = transform(xyzB)

    // May be out of range [0..255] as HCL have wider range.
    // coerceIn(0, 255) works fine and gives same RGB as R (ggplot2) for values out of range.
    //
    // Test:
    // https://www.easyrgb.com/en/convert.php#inputFORM (displays real sRGB without clamping):
    // CIE-L*Ch(uv): L=95, C(uv)=100, h(uv)=205.0
    //  => sRGB = -843.519  271.922  297.628 (R and B are out of range)
    //  => CSS = #00FFFF (clamped)
    //
    // R:
    // grDevices::hcl(h=205, c=100, l=95)
    //  => '#00FFFF' (clamped)
    return Color(
        (r * 255).roundToInt().coerceIn(0, 255),
        (g * 255).roundToInt().coerceIn(0, 255),
        (b * 255).roundToInt().coerceIn(0, 255)
    )
}


/**
 * References:
 * http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
 * https://www.easyrgb.com/en/math.php
 */
fun xyzFromRgb(rgb: Color): XYZ {
    fun transform(v: Double): Double = when {
        v > 0.04045 -> ((v + 0.055) / 1.055).pow(2.4)
        else -> v / 12.92
    }

    val red = transform(rgb.red / 255.0)
    val green = transform(rgb.green / 255.0)
    val blue = transform(rgb.blue / 255.0)

    // D65 sRGB matrix
    return XYZ(
        x = (red * 0.4124 + green * 0.3576 + blue * 0.1805) * 100.0,
        y = (red * 0.2126 + green * 0.7152 + blue * 0.0722) * 100.0,
        z = (red * 0.0193 + green * 0.1192 + blue * 0.9505) * 100.0
    )
}
