/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Font

class PlotLabelSpec(font: Font, widthScaleFactor: Double = 1.0) :
    LabelSpec {
    private val myLabelMetrics: LabelMetrics = LabelMetrics(font, widthScaleFactor)

    override val font: Font
        get() = myLabelMetrics.font

    override val isMonospaced: Boolean
        get() = myLabelMetrics.isMonospaced

    override val widthScaleFactor: Double
        get() = myLabelMetrics.widthScaleFactor

    override fun dimensions(labelText: String): DoubleVector {
        return myLabelMetrics.dimensions(labelText)
    }

    override fun width(labelText: String): Double {
        return myLabelMetrics.width(labelText)
    }

    override fun height(): Double {
        return myLabelMetrics.height()
    }
}
