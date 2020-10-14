/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

import kotlin.math.abs

/**
 * @param h hue, degree. Normal range: [0, 360]
 * @param saturation saturation, [0, 1]
 * @param value value, [0, 1]
 */
class HSV(val h: Double, saturation: Double, value: Double = 1.0) {
    val s: Double
    val v: Double

    init {
        require(saturation >= -0.001 && saturation <= 1.001) { "HSV 'saturation' must be in range [0, 1] but was $saturation" }
        require(value >= -0.001 && value <= 1.001) { "HSV 'value' must be in range [0, 1] but was $value" }

        s = abs((saturation * 100).toInt() / 100.0)
        v = abs((value * 100).toInt() / 100.0)
    }

    override fun toString(): String {
        return "HSV($h, $s, $v)"
    }
}