/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

internal object AttributeUtil {

    private fun doubleOrDefault(e: SlimBase, attrIndex: Int, d: Double): Double {
        val o = e.getAttribute(attrIndex)
        if (o == null) {
            return d
        } else if (o is Number) {
            return o.toDouble()
        }
        return o.toString().toDouble()
    }

    fun zeroIfNull(e: SlimBase, attrIndex: Int): Double {
        return doubleOrDefault(e, attrIndex, 0.0)
    }

    fun oneIfNull(e: SlimBase, attrIndex: Int): Double {
        return doubleOrDefault(e, attrIndex, 1.0)
    }

    fun stringOrNull(e: SlimBase, attrIndex: Int): String? {
        return e.getAttribute(attrIndex)?.toString()
    }
}
