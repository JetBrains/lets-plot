/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colormodel

/**
 * Lightness, u, v
 * l: 0..100
 * u: -100..100 (may exceed limit)
 * v: -100..100 (may exceed limit)
 */
class LUV(
    val l: Double,
    val u: Double,
    val v: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LUV

        if (l != other.l) return false
        if (u != other.u) return false
        if (v != other.v) return false

        return true
    }

    override fun hashCode(): Int {
        var result = l.hashCode()
        result = 31 * result + u.hashCode()
        result = 31 * result + v.hashCode()
        return result
    }

    override fun toString(): String {
        return "LUV(l=$l, u=$u, v=$v)"
    }
}
