/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil
import jetbrains.datalore.plot.builder.theme.LegendTheme
import kotlin.math.max

abstract class LegendComponentLayout(
    title: String,
    protected val breaks: List<LegendBreak>,
    val keySizes: List<DoubleVector>,
    legendDirection: LegendDirection,
    theme: LegendTheme
) : LegendBoxLayout(title, legendDirection, theme) {

    private var myContentSize: DoubleVector? = null
    private val myKeyLabelBoxes = ArrayList<DoubleRectangle>()
    private val myLabelBoxes = ArrayList<DoubleRectangle>()

    var isFillByRow = false
    var rowCount = 0
        set(rowCount) {
            check(rowCount > 0) { "Row count must be greater than 0, was $rowCount" }
            field = rowCount
        }
    var colCount = 0
        set(colCount) {
            check(colCount > 0) { "Col count must be greater than 0, was $colCount" }
            field = colCount
        }

    override val graphSize: DoubleVector
        get() {
            ensureInited()
            return myContentSize!!
        }

    val keyLabelBoxes: List<DoubleRectangle>
        get() {
            ensureInited()
            return myKeyLabelBoxes
        }

    val labelBoxes: List<DoubleRectangle>
        get() {
            ensureInited()
            return myLabelBoxes
        }

    private fun ensureInited() {
        if (myContentSize == null) {
            doLayout()
        }
    }

    private fun doLayout() {
        val labelLeftMargin = PlotLabelSpecFactory.legendItem(theme).width(PlotLabelSpecFactory.DISTANCE_TO_LABEL_IN_CHARS) / 2
        val labelVerticalDistance = PlotLabelSpecFactory.legendItem(theme).height() / 3

        val contentOrigin = DoubleVector.ZERO
        var breakBoxBounds: DoubleRectangle? = null
        for (i in breaks.indices) {
            val labelSize = labelSize(i).add(DoubleVector(0.0, labelVerticalDistance))
            val keySize = keySizes[i]
            val height = max(keySize.y, labelSize.y)
            val labelVOffset = (height - labelSize.y) / 2
            val labelHOffset = keySize.x + labelLeftMargin
            val breakBoxSize = DoubleVector(labelHOffset + labelSize.x, height)
            breakBoxBounds = DoubleRectangle(
                breakBoxBounds?.let { breakBoxOrigin(i, it) } ?: contentOrigin,
                breakBoxSize
            )

            myKeyLabelBoxes.add(breakBoxBounds)
            myLabelBoxes.add(
                DoubleRectangle(
                    labelHOffset, labelVOffset,
                    labelSize.x, labelSize.y
                )
            )
        }

        myContentSize = GeometryUtil.union(DoubleRectangle(contentOrigin, DoubleVector.ZERO), myKeyLabelBoxes).dimension
    }

    protected abstract fun breakBoxOrigin(index: Int, prevBreakBoxBounds: DoubleRectangle): DoubleVector

    protected abstract fun labelSize(index: Int): DoubleVector

    private class MyHorizontal internal constructor(
        title: String,
        breaks: List<LegendBreak>,
        keySizes: List<DoubleVector>,
        theme: LegendTheme
    ) : LegendComponentLayout(
        title, breaks, keySizes,
        LegendDirection.HORIZONTAL,
        theme
    ) {
        init {
            colCount = breaks.size
            rowCount = 1
        }

        override fun breakBoxOrigin(index: Int, prevBreakBoxBounds: DoubleRectangle): DoubleVector {
            return DoubleVector(prevBreakBoxBounds.right, 0.0)
        }

        override fun labelSize(index: Int): DoubleVector {
            val label = breaks[index].label
            return PlotLayoutUtil.textDimensions(label, PlotLabelSpecFactory.legendItem(theme))
        }
    }

    private class MyHorizontalMultiRow internal constructor(
        title: String,
        breaks: List<LegendBreak>,
        keySizes: List<DoubleVector>,
        theme: LegendTheme
    ) : MyMultiRow(
        title, breaks, keySizes,
        LegendDirection.HORIZONTAL,
        theme
    ) {
        init {
            colCount = breaks.size
            rowCount = 1
        }
    }

    private class MyVertical internal constructor(
        title: String,
        breaks: List<LegendBreak>,
        keySizes: List<DoubleVector>,
        theme: LegendTheme
    ) : MyMultiRow(
        title, breaks, keySizes,
        LegendDirection.VERTICAL,
        theme
    ) {
        init {
            colCount = 1
            rowCount = breaks.size
        }
    }

    private abstract class MyMultiRow internal constructor(
        title: String,
        breaks: List<LegendBreak>,
        keySizes: List<DoubleVector>,
        legendDirection: LegendDirection,
        theme: LegendTheme
    ) : LegendComponentLayout(title, breaks, keySizes, legendDirection, theme) {
        private var myMaxLabelWidth = 0.0

        init {
            for (br in breaks) {
                myMaxLabelWidth = max(
                    myMaxLabelWidth,
                    PlotLayoutUtil.textDimensions(br.label, PlotLabelSpecFactory.legendItem(theme)).x
                )
            }
        }

        override fun breakBoxOrigin(index: Int, prevBreakBoxBounds: DoubleRectangle): DoubleVector {
            if (isFillByRow) {
                return if (index % colCount == 0) {
                    DoubleVector(0.0, prevBreakBoxBounds.bottom)
                } else DoubleVector(prevBreakBoxBounds.right, prevBreakBoxBounds.top)
            }

            // fill by column
            return if (index % rowCount == 0) {
                DoubleVector(prevBreakBoxBounds.right, 0.0)
            } else DoubleVector(prevBreakBoxBounds.left, prevBreakBoxBounds.bottom)

        }

        override fun labelSize(index: Int): DoubleVector {
            return DoubleVector(
                myMaxLabelWidth,
                PlotLayoutUtil.textDimensions(breaks[index].label, PlotLabelSpecFactory.legendItem(theme)).y
            )
        }
    }

    companion object {
        fun horizontal(title: String, breaks: List<LegendBreak>, keySizes: List<DoubleVector>, theme: LegendTheme): LegendComponentLayout {
            return MyHorizontal(
                title,
                breaks,
                keySizes,
                theme
            )
        }

        fun horizontalMultiRow(title: String, breaks: List<LegendBreak>, keySizes: List<DoubleVector>, theme: LegendTheme): LegendComponentLayout {
            return MyHorizontalMultiRow(
                title,
                breaks,
                keySizes,
                theme
            )
        }

        fun vertical(title: String, breaks: List<LegendBreak>, keySizes: List<DoubleVector>, theme: LegendTheme): LegendComponentLayout {
            return MyVertical(
                title,
                breaks,
                keySizes,
                theme
            )
        }
    }
}
