/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

class QuadKey(val string: String) {
    fun zoom(): Int {
        return ((string.length - 1) / 3 + 1) * 3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val quadKey = other as QuadKey?
        return string == quadKey!!.string
    }

    override fun hashCode(): Int {
        return listOf(string).hashCode()
    }

    override fun toString(): String {
        return string
    }
}
