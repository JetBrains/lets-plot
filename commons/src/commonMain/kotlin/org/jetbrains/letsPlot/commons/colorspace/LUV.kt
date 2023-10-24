/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace

import kotlin.math.cbrt
import kotlin.math.pow

/**
 * Lightness, u, v
 *  l: 0..100
 *  u: -100..100 (may exceed limit)
 *  v: -100..100 (may exceed limit)
 */
data class LUV(
    val l: Double,
    val u: Double,
    val v: Double
)

/**
 * References:
 * http://www.brucelindbloom.com/index.html?Eqn_Luv_to_XYZ.html
 * https://www.easyrgb.com/en/math.php
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
