/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.math.abs
import kotlin.math.roundToInt

class HSL(
    hue: Double,
    saturation: Double,
    lightness: Double,
) {
    val h: Double
    val s: Double
    val l: Double

    init {
        require(saturation >= -0.001 && saturation <= 1.001) { "HSL 'saturation' must be in range [0, 1] but was $saturation" }
        require(lightness >= -0.001 && lightness <= 1.001) { "HSL 'lightness' must be in range [0, 1] but was $lightness" }

        // Trim precision
        h = (hue * 100).roundToInt() / 100.0
        s = abs((saturation * 100).roundToInt() / 100.0)
        l = abs((lightness * 100).roundToInt() / 100.0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HSL

        if (h != other.h) return false
        if (s != other.s) return false
        if (l != other.l) return false

        return true
    }

    override fun hashCode(): Int {
        var result = h.hashCode()
        result = 31 * result + s.hashCode()
        result = 31 * result + l.hashCode()
        return result
    }

    override fun toString(): String {
        return "HSL(h=$h, s=$s, l=$l)"
    }

}
