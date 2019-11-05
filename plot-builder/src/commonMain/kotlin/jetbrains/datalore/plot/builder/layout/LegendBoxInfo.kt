/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

abstract class LegendBoxInfo protected constructor(internal val size: DoubleVector) {

    open val isEmpty: Boolean
        get() = false

    abstract fun createLegendBox(): jetbrains.datalore.plot.builder.guide.LegendBox

    companion object {
        val EMPTY: LegendBoxInfo = object : LegendBoxInfo(DoubleVector.ZERO) {
            override val isEmpty: Boolean
                get() = true

            override fun createLegendBox(): jetbrains.datalore.plot.builder.guide.LegendBox {
                throw IllegalStateException("Empty legend box info")
            }
        }
    }

}
