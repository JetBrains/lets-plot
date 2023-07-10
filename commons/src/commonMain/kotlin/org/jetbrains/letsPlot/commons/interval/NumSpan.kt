/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.interval

abstract class NumSpan {
    abstract val lowerEnd: Number
    abstract val upperEnd: Number

    override fun toString(): String {
        return "${this::class.simpleName}($lowerEnd, $upperEnd)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NumSpan

        if (lowerEnd != other.lowerEnd) return false
        if (upperEnd != other.upperEnd) return false
        return true
    }

    override fun hashCode(): Int {
        return lowerEnd.hashCode() + 31 * upperEnd.hashCode()
    }
}