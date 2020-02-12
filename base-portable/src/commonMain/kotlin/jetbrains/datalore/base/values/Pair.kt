/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

// ToDo: use Kotlin Pair
class Pair<FirstT, SecondT>(val first: FirstT, val second: SecondT) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Pair<*, *>

        if (first != other.first) return false
        if (second != other.second) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "[$first, $second]"
    }

    operator fun component1() = first
    operator fun component2() = second
}
