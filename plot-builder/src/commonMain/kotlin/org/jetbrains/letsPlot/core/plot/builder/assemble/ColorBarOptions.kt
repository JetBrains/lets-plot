/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

class ColorBarOptions constructor(
    val width: Double? = null,
    val height: Double? = null,
    val binCount: Int? = null,
    title: String? = null
) : GuideOptions(title) {

    override fun withTitle(title: String?): ColorBarOptions {
        return ColorBarOptions(
            width, height, binCount, title = title
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ColorBarOptions

        if (width != other.width) return false
        if (height != other.height) return false
        if (binCount != other.binCount) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width?.hashCode() ?: 0
        result = 31 * result + (height?.hashCode() ?: 0)
        result = 31 * result + (binCount ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }
}
