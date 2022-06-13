/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector

class PlotLabelSpec(fontSize: Double, bold: Boolean = false, monospaced: Boolean = false) :
    LabelSpec {
    private val myLabelMetrics: LabelMetrics = LabelMetrics(fontSize, bold, monospaced)

    override val isBold: Boolean
        get() = myLabelMetrics.isBold

    override val isMonospaced: Boolean
        get() = myLabelMetrics.isMonospaced

    override val fontSize: Double
        get() = myLabelMetrics.fontSize

    override fun dimensions(labelLength: Int): DoubleVector {
        return myLabelMetrics.dimensions(labelLength)
    }

    override fun width(labelLength: Int): Double {
        return myLabelMetrics.width(labelLength)
    }

    override fun height(): Double {
        return myLabelMetrics.height()
    }
}
