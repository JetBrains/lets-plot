/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot

enum class PlotLabelSpec(fontSize: Double, bold: Boolean = false, monospaced: Boolean = false) :
    LabelSpec {
    PLOT_TITLE(Defaults.Common.Title.FONT_SIZE.toDouble(), true),

    AXIS_TICK(Plot.Axis.TICK_FONT_SIZE.toDouble()),
    AXIS_TICK_SMALL(Plot.Axis.TICK_FONT_SIZE_SMALL.toDouble()),
    AXIS_TITLE(Plot.Axis.TITLE_FONT_SIZE.toDouble()),

    LEGEND_TITLE(Defaults.Common.Legend.TITLE_FONT_SIZE.toDouble()),
    LEGEND_ITEM(Defaults.Common.Legend.ITEM_FONT_SIZE.toDouble());

    private val myLabelMetrics: LabelMetrics

    override val isBold: Boolean
        get() = myLabelMetrics.isBold

    override val isMonospaced: Boolean
        get() = myLabelMetrics.isMonospaced

    override val fontSize: Double
        get() = myLabelMetrics.fontSize

    init {
        myLabelMetrics =
            LabelMetrics(fontSize, bold, monospaced)
    }

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
/**
 * @param fontSize in 'px' (same meaning as in CSS)
 */
