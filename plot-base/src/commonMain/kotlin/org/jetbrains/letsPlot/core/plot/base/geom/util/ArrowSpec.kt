/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType

/**
 * @param angle  The angle of the arrow head in radians (smaller numbers produce narrower, pointier arrows).
 * Essentially describes the width of the arrow head.
 * @param length The length of the arrow head (px).
 */
class ArrowSpec(
    val angle: Double,
    val length: Double,
    val end: End,
    val type: Type
) {

    val isOnFirstEnd: Boolean
        get() = end == End.FIRST || end == End.BOTH

    val isOnLastEnd: Boolean
        get() = end == End.LAST || end == End.BOTH

    enum class End {
        LAST, FIRST, BOTH
    }

    enum class Type {
        OPEN, CLOSED
    }

    companion object {
        internal fun ArrowSpec.toArrowAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                private val filled = (type == Type.CLOSED)

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.FILL -> if (filled) super.get(Aes.COLOR) else Color.TRANSPARENT
                        Aes.LINETYPE -> NamedLineType.SOLID // avoid ugly patterns if linetype is other than 'solid'
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
    }
}
