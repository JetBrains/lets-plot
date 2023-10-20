/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colormodel


/**
 * CIE XYZ
 * x: 0..100 (may exceed limit)
 * y: 0..100 (may exceed limit)
 * z: 0..100 (may exceed limit)
 */
class XYZ(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as XYZ

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "XYZ(x=$x, y=$y, z=$z)"
    }
}

