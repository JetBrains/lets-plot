/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colormodel


/**
 * Hue, Chroma, Lightness
 *  h: 0..360
 *  c: 0..100
 *  l: 0..100
 */
class HCL(
    val h: Double,
    val c: Double,
    val l: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HCL

        if (h != other.h) return false
        if (c != other.c) return false
        if (l != other.l) return false

        return true
    }

    override fun hashCode(): Int {
        var result = h.hashCode()
        result = 31 * result + c.hashCode()
        result = 31 * result + l.hashCode()
        return result
    }

    override fun toString(): String {
        return "HCL(h=$h, c=$c, l=$l)"
    }
}
