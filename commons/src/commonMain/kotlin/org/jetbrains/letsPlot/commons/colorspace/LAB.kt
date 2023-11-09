/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace

import kotlin.math.cbrt
import kotlin.math.pow

/**
 * Lightness, Green-Red, Blue-Yellow
 *  L: 0..100
 *  a: -100..100
 *  b: -100..100
*/
data class LAB(
    val l: Double,
    val a: Double,
    val b: Double,
)


/**
 * Reference:
 * https://www.easyrgb.com/en/math.php
 */
fun labFromXyz(xyz: XYZ): LAB {
    fun transform(v: Double) = if (v > 0.008856) cbrt(v) else (7.787 * v) + (16.0 / 116.0)

    val preA = transform(xyz.x / referenceX)
    val preL = transform(xyz.y / referenceY)
    val preB = transform(xyz.z / referenceZ)

    val l = (116.0 * preL) - 16.0
    val a = 500.0 * (preA - preL)
    val b = 200.0 * (preL - preB)

    return LAB(l, a, b)
}


/**
 * Reference:
 * https://www.easyrgb.com/en/math.php
 */
fun xyzFromLab(lab: LAB): XYZ {
    fun transform(v: Double) = if (v.pow(3) > 0.008856) v.pow(3) else (v - 16.0 / 116.0) / 7.787

    val preY = (lab.l + 16.0) / 116.0
    val preX = lab.a / 500.0 + preY
    val preZ = preY - lab.b / 200.0

    val x = transform(preX) * referenceX
    val y = transform(preY) * referenceY
    val z = transform(preZ) * referenceZ

    return XYZ(x, y, z)
}
