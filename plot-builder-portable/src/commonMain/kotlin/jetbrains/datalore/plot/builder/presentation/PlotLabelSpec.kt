/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.StyleSheet
import jetbrains.datalore.vis.TextStyle

class PlotLabelSpec(fontSize: Double, bold: Boolean = false, monospaced: Boolean = false) :
    LabelSpec {

    constructor(textStyle: TextStyle) : this(textStyle.size, textStyle.face.bold, monospaced = false)

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

    companion object {
        private fun StyleSheet.getPlotLabelSpec(className: String) = PlotLabelSpec(getTextStyle(className))

        fun plotTitle(styleSheet: StyleSheet) = styleSheet.getPlotLabelSpec(Style.PLOT_TITLE)
        fun plotSubtitle(styleSheet: StyleSheet) = styleSheet.getPlotLabelSpec(Style.PLOT_SUBTITLE)
        fun plotCaption(styleSheet: StyleSheet) = styleSheet.getPlotLabelSpec(Style.PLOT_CAPTION)

        fun axisTick(styleSheet: StyleSheet, axis: String) = styleSheet.getPlotLabelSpec("${Style.AXIS_TEXT}-${axis}")
        fun axisTitle(styleSheet: StyleSheet, axis: String) = styleSheet.getPlotLabelSpec("${Style.AXIS_TITLE}-${axis}")

        fun legendItem(styleSheet: StyleSheet) = styleSheet.getPlotLabelSpec(Style.LEGEND_ITEM)
        fun legendTitle(styleSheet: StyleSheet) = styleSheet.getPlotLabelSpec(Style.LEGEND_TITLE)
    }
}