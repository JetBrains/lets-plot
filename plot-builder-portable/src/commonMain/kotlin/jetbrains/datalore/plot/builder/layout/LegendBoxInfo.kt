/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.LegendBox

abstract class LegendBoxInfo(
    internal val size: DoubleVector
) {

    open val isEmpty: Boolean
        get() = false

    abstract fun createLegendBox(): LegendBox

    companion object {
        val EMPTY: LegendBoxInfo = object : LegendBoxInfo(DoubleVector.ZERO) {
            override val isEmpty: Boolean
                get() = true

            override fun createLegendBox(): LegendBox {
                throw IllegalStateException("Empty legend box info")
            }
        }
    }

}
