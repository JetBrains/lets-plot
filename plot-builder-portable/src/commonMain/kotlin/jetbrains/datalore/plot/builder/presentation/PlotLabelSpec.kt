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
        // ToDo: Temp solution!
        //  Need to remove this dictionary and getters,
        //  will get metrics via theme

        private val labelSpecs = HashMap<String, PlotLabelSpec>()

        fun initWithStyleSheet(styleSheet: StyleSheet) {
            fun putLabelSpec(className: String) {
                labelSpecs[className] = PlotLabelSpec(styleSheet.getTextStyle(className))
            }

            putLabelSpec(Style.PLOT_TITLE)
            putLabelSpec(Style.PLOT_SUBTITLE)
            putLabelSpec(Style.PLOT_CAPTION)

            putLabelSpec("${Style.AXIS_TEXT}-x")
            putLabelSpec("${Style.AXIS_TEXT}-y")
            putLabelSpec("${Style.AXIS_TITLE}-x")
            putLabelSpec("${Style.AXIS_TITLE}-y")

            putLabelSpec(Style.LEGEND_TITLE)
            putLabelSpec(Style.LEGEND_ITEM)
        }

        private fun get(className: String) = labelSpecs[className] ?: PlotLabelSpec(0.0)

        val PLOT_TITLE: PlotLabelSpec
            get() = get(Style.PLOT_TITLE)

        val PLOT_SUBTITLE: PlotLabelSpec
            get() = get(Style.PLOT_SUBTITLE)

        val PLOT_CAPTION: PlotLabelSpec
            get() = get(Style.PLOT_CAPTION)

        val LEGEND_TITLE: PlotLabelSpec
            get() = get(Style.LEGEND_TITLE)

        val LEGEND_ITEM: PlotLabelSpec
            get() = get(Style.LEGEND_ITEM)

        fun axisTick(axis: String) = get("${Style.AXIS_TEXT}-$axis")

        fun axisTitle(axis: String) = get("${Style.AXIS_TITLE}-$axis")
    }
}