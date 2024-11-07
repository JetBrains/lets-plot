/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

class Floating(i: Int, fraction: String, e: Int) {
    val i: Int // 0..9
    val fraction: String // never empty
    val e: Int

    init {
        require(i in 0..9) { "i should be in 0..9" }
        this.i = i
        this.fraction = fraction.takeIf { it.isNotEmpty() } ?: "0"
        this.e = e
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Floating

        if (i != other.i) return false
        if (fraction != other.fraction) return false
        if (e != other.e) return false

        return true
    }

    override fun hashCode(): Int {
        var result = i
        result = 31 * result + fraction.hashCode()
        result = 31 * result + e
        return result
    }

    override fun toString(): String {
        return "Floating(i=$i, fraction='$fraction', e=$e)"
    }
}