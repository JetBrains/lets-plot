/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @param hue Normal range: [0, 360] degrees.
 * @param saturation [0, 1]
 * @param value [0, 1]
 */
class HSV(hue: Double, saturation: Double, value: Double = 1.0) {
    val h: Double
    val s: Double
    val v: Double

    init {
        require(saturation >= -0.001 && saturation <= 1.001) { "HSV 'saturation' must be in range [0, 1] but was $saturation" }
        require(value >= -0.001 && value <= 1.001) { "HSV 'value' must be in range [0, 1] but was $value" }

        // Trim precision
        h = (hue * 100).roundToInt() / 100.0
        s = abs((saturation * 100).roundToInt() / 100.0)
        v = abs((value * 100).roundToInt() / 100.0)
    }

    override fun toString(): String {
        return "HSV($h, $s, $v)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HSV

        if (h != other.h) return false
        if (s != other.s) return false
        if (v != other.v) return false

        return true
    }

    override fun hashCode(): Int {
        var result = h.hashCode()
        result = 31 * result + s.hashCode()
        result = 31 * result + v.hashCode()
        return result
    }
}