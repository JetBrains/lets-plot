/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.GeometryUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
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

    private fun indexToPosition(i: Int): Pair<Int, Int> = if (isFillByRow) {
        val row = i / colCount
        val col = i % colCount
        row to col
    } else {
        val col = i / rowCount
        val row = i % rowCount
        row to col
    }

    private fun doLayout() {
        val labelSpec = PlotLabelSpecFactory.legendItem(theme)
        val keyLabelGap = labelSpec.width(PlotLabelSpecFactory.DISTANCE_TO_LABEL_IN_CHARS) / 2.0
        val defaultSpacing = DoubleVector(keyLabelGap, labelSpec.height() / 3.0)
        val spacingBetweenLabels = theme.keySpacing().add(defaultSpacing)

        val colWidths = DoubleArray(colCount)
        val rowHeights = DoubleArray(rowCount)

        keySizes.forEachIndexed { i, keySize ->
            val (row, col) = indexToPosition(i)
            val labelSize = labelSize(i)
            val labelOffset = DoubleVector(keySize.x + keyLabelGap, keySize.y / 2)
            myLabelBoxes += DoubleRectangle(labelOffset, labelSize)

            colWidths[col] = maxOf(colWidths[col], labelOffset.x + labelSize.x)
            rowHeights[row] = maxOf(rowHeights[row], keySize.y, labelSize.y)
        }

        val colX = colWidths.runningFold(0.0) { acc, w -> acc + w + spacingBetweenLabels.x }
        val rowY = rowHeights.runningFold(0.0) { acc, h -> acc + h + spacingBetweenLabels.y }

        breaks.indices.forEach { i ->
            val (row, col) = indexToPosition(i)
            val breakBoxBounds = DoubleRectangle(colX[col], rowY[row], colWidths[col], rowHeights[row])
            myKeyLabelBoxes.add(breakBoxBounds)
        }

        myContentSize = GeometryUtil.union(DoubleRectangle.ZERO, myKeyLabelBoxes).dimension
    }

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

        override fun labelSize(index: Int): DoubleVector {
            return PlotLayoutUtil.textDimensions(breaks[index].label, PlotLabelSpecFactory.legendItem(theme))
        }
    }

    companion object {
        fun horizontal(
            title: String,
            breaks: List<LegendBreak>,
            keySizes: List<DoubleVector>,
            theme: LegendTheme
        ): LegendComponentLayout {
            return MyHorizontal(
                title,
                breaks,
                keySizes,
                theme
            )
        }

        fun horizontalMultiRow(
            title: String,
            breaks: List<LegendBreak>,
            keySizes: List<DoubleVector>,
            theme: LegendTheme
        ): LegendComponentLayout {
            return MyHorizontalMultiRow(
                title,
                breaks,
                keySizes,
                theme
            )
        }

        fun vertical(
            title: String,
            breaks: List<LegendBreak>,
            keySizes: List<DoubleVector>,
            theme: LegendTheme
        ): LegendComponentLayout {
            return MyVertical(
                title,
                breaks,
                keySizes,
                theme
            )
        }
    }
}
