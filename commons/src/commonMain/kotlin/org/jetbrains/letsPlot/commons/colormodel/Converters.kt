/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colormodel

import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.*


private const val referenceX = 0.95047 * 100.0
private const val referenceY = 1.00000 * 100.0
private const val referenceZ = 1.08883 * 100.0

private const val cieE = 0.008856

fun rgbFromHcl(hcl: HCL, alpha: Double = 1.0): Color {
    val luv = luvFromHcl(hcl)
    val xyz = xyzFromLuv(luv)
    val rgb = rgbFromXyz(xyz)
    return rgb.changeAlpha((255 * alpha).roundToInt())
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


fun hclFromRgb(rgb: Color): HCL {
    val xyz = xyzFromRgb(rgb)
    val luv = luvFromXyz(xyz)
    val hcl = hclFromLuv(luv)
    return hcl
}


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
        hue = if (h >= 0.0) h else h + 360.0,
        saturation = s,
        lightness = l
    )
}

/*
* References:
* https://en.wikipedia.org/wiki/CIELUV
* https://www.easyrgb.com/en/math.php
 */
fun hclFromLuv(luv: LUV): HCL {
    val c = sqrt(luv.u * luv.u + luv.v * luv.v)
    val h = toDegrees(atan2(luv.v, luv.u)) % 360.0

    return HCL(
        h = h,
        c = c,
        l = luv.l
    )
}


/*
* References:
* https://en.wikipedia.org/wiki/CIELUV
* https://observablehq.com/@mbostock/luv-and-hcl
* https://www.easyrgb.com/en/math.php (CIE-L*CH° → CIE-L*ab)
 */
fun luvFromHcl(hcl: HCL): LUV {
    val hDegrees = toRadians(hcl.h)
    val u = hcl.c * cos(hDegrees)
    val v = hcl.c * sin(hDegrees)

    return LUV(
        l = hcl.l,
        u = u,
        v = v
    )
}


/**
 * References:
 * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
 * https://www.easyrgb.com/en/math.php
 */
fun luvFromXyz(xyz: XYZ): LUV {
    val inputDen = xyz.x + 15.0 * xyz.y + 3.0 * xyz.z
    val preU = if (inputDen == 0.0) 0.0 else (4 * xyz.x) / inputDen
    val preV = if (inputDen == 0.0) 0.0 else (9 * xyz.y) / inputDen

    val y = xyz.y / 100.0
    val preL = when {
        y > cieE -> cbrt(y)
        else -> (7.787 * y) + (16.0 / 116.0)
    }

    val refDen = referenceX + (15.0 * referenceY) + (3.0 * referenceZ)
    val refU = (4.0 * referenceX) / refDen
    val refV = (9.0 * referenceY) / refDen

    val l = (116.0 * preL) - 16.0
    val u = 13.0 * l * (preU - refU)
    val v = 13.0 * l * (preV - refV)

    return LUV(
        l = l.coerceIn(0.0, 100.0),
        u = u,
        v = v
    )
}


/*
References:
http://www.brucelindbloom.com/index.html?Eqn_Luv_to_XYZ.html
https://www.easyrgb.com/en/math.php
 */
fun xyzFromLuv(luv: LUV): XYZ {
    if (luv.l == 0.0) {
        return XYZ(0.0, 0.0, 0.0)
    }

    val l = (luv.l + 16.0) / 116.0
    val preY = when {
        l.pow(3) > 0.008856 -> l.pow(3)
        else -> (l - 16.0 / 116.0) / 7.787
    }

    val refDen = referenceX + 15.0 * referenceY + 3.0 * referenceZ
    val refU = (4.0 * referenceX) / refDen
    val refV = (9.0 * referenceY) / refDen

    val u = luv.u / (13.0 * luv.l) + refU
    val v = luv.v / (13.0 * luv.l) + refV

    val y = preY * 100.0
    val x = -(9.0 * y * u) / ((u - 4.0) * v - u * v)
    val z = (9.0 * y - (15.0 * v * y) - (v * x)) / (3.0 * v)

    return XYZ(
        x = x,
        y = y,
        z = z
    )
}


/*
References:
http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_RGB.html
https://www.easyrgb.com/en/math.php
 */
fun rgbFromXyz(xyz: XYZ): Color {
    fun convert(v: Double): Double = when {
        v > 0.0031308 -> 1.055 * (v.pow(1 / 2.4)) - 0.055
        else -> 12.92 * v
    }

    val x = xyz.x / 100.0
    val y = xyz.y / 100.0
    val z = xyz.z / 100.0

    val xyzR = x * 3.2406 + y * -1.5372 + z * -0.4986
    val xyzG = x * -0.9689 + y * 1.8758 + z * 0.0415
    val xyzB = x * 0.0557 + y * -0.2040 + z * 1.0570

    val r = convert(xyzR)
    val g = convert(xyzG)
    val b = convert(xyzB)

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


/*
References:
http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
https://www.easyrgb.com/en/math.php
 */
fun xyzFromRgb(rgb: Color): XYZ {
    fun convert(v: Double): Double = when {
        v > 0.04045 -> ((v + 0.055) / 1.055).pow(2.4)
        else -> v / 12.92
    }

    val red = convert(rgb.red / 255.0)
    val green = convert(rgb.green / 255.0)
    val blue = convert(rgb.blue / 255.0)

    // D65 sRGB matrix
    return XYZ(
        x = (red * 0.4124 + green * 0.3576 + blue * 0.1805) * 100.0,
        y = (red * 0.2126 + green * 0.7152 + blue * 0.0722) * 100.0,
        z = (red * 0.0193 + green * 0.1192 + blue * 0.9505) * 100.0
    )
}
