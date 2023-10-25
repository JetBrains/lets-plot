/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace

import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.*


fun rgbFromHcl(hcl: HCL, alpha: Double = 1.0): Color {
    val luv = luvFromHcl(hcl)
    val xyz = xyzFromLuv(luv)
    val rgb = rgbFromXyz(xyz)
    return rgb.changeAlpha((255 * alpha).roundToInt())
}


fun rgbFromLab(lab: LAB, alpha: Double = 1.0): Color {
    val xyz = xyzFromLab(lab)
    val rgb = rgbFromXyz(xyz)
    return rgb.changeAlpha((255 * alpha).roundToInt())
}


fun hclFromRgb(rgb: Color): HCL {
    val xyz = xyzFromRgb(rgb)
    val luv = luvFromXyz(xyz)
    val hcl = hclFromLuv(luv)
    return hcl
}


fun labFromRgb(rgb: Color): LAB {
    val xyz = xyzFromRgb(rgb)
    val lab = labFromXyz(xyz)
    return lab
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
        h = if (h < 0) h + 360.0 else h,
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
