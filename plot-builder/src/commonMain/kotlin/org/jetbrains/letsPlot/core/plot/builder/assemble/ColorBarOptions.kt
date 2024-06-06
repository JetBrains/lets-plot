/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

class ColorBarOptions constructor(
    val width: Double? = null,
    val height: Double? = null,
    val binCount: Int? = null,
    isReverse: Boolean = false,
    title: String? = null
) : GuideOptions(isReverse, title) {

    override fun withReverse(reverse: Boolean): ColorBarOptions {
        return ColorBarOptions(
            width, height, binCount, isReverse = reverse, title
        )
    }

    override fun withTitle(title: String?): GuideOptions {
        return ColorBarOptions(
            width, height, binCount, isReverse, title = title
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ColorBarOptions

        if (width != other.width) return false
        if (height != other.height) return false
        if (binCount != other.binCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width?.hashCode() ?: 0
        result = 31 * result + (height?.hashCode() ?: 0)
        result = 31 * result + (binCount ?: 0)
        return result
    }
}
