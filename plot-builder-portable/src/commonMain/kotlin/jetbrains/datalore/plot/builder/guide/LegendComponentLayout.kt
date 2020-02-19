/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import kotlin.math.max

abstract class LegendComponentLayout(title: String,
                                     protected val breaks: List<LegendBreak>,
                                     val keySize: DoubleVector,
                                     legendDirection: LegendDirection
) : LegendBoxLayout(title, legendDirection) {

    private var myContentSize: DoubleVector? = null
    private val myKeyLabelBoxes = ArrayList<DoubleRectangle>()
    private val myLabelBoxes = ArrayList<DoubleRectangle>()

    var isFillByRow = false
    var rowCount = 0
        set(rowCount) {
            checkState(rowCount > 0, "Row count must be greater than 0, was $rowCount")
            field = rowCount
        }
    var colCount = 0
        set(colCount) {
            checkState(colCount > 0, "Col count must be greater than 0, was $colCount")
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
        val labelHeight = LABEL_SPEC.height()
        val labelLeftMargin = LABEL_SPEC.width(1) / 2
        val labelHOffset = keySize.x + labelLeftMargin
        val labelVOffset = (keySize.y - labelHeight) / 2

        val contentOrigin = DoubleVector.ZERO
        var breakBoxBounds: DoubleRectangle? = null
        for (i in breaks.indices) {
            val labelSize = labelSize(i)
            val breakBoxSize = DoubleVector(labelHOffset + labelSize.x, keySize.y)
            breakBoxBounds = DoubleRectangle(
                    breakBoxBounds?.let { breakBoxOrigin(i, it) } ?: contentOrigin,
                    breakBoxSize
            )

            myKeyLabelBoxes.add(breakBoxBounds)
            myLabelBoxes.add(DoubleRectangle(
                    labelHOffset, labelVOffset,
                    labelSize.x, labelSize.y
            ))
        }

        myContentSize = GeometryUtil.union(DoubleRectangle(contentOrigin, DoubleVector.ZERO), myKeyLabelBoxes).dimension
    }

    protected abstract fun breakBoxOrigin(index: Int, prevBreakBoxBounds: DoubleRectangle): DoubleVector

    protected abstract fun labelSize(index: Int): DoubleVector

    private class MyHorizontal internal constructor(title: String, breaks: List<LegendBreak>, keySize: DoubleVector) : LegendComponentLayout(title, breaks, keySize,
        LegendDirection.HORIZONTAL
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
            return DoubleVector(LABEL_SPEC.width(label.length), LABEL_SPEC.height())
        }
    }

    private class MyHorizontalMultiRow internal constructor(title: String, breaks: List<LegendBreak>, keySize: DoubleVector) : MyMultiRow(title, breaks, keySize,
        LegendDirection.HORIZONTAL
    ) {
        init {
            colCount = breaks.size
            rowCount = 1
        }
    }

    private class MyVertical internal constructor(title: String, breaks: List<LegendBreak>, keySize: DoubleVector) : MyMultiRow(title, breaks, keySize,
        LegendDirection.VERTICAL
    ) {
        init {
            colCount = 1
            rowCount = breaks.size
        }
    }

    private abstract class MyMultiRow internal constructor(title: String, breaks: List<LegendBreak>, keySize: DoubleVector, legendDirection: LegendDirection) : LegendComponentLayout(title, breaks, keySize, legendDirection) {
        private var myMaxLabelWidth = 0.0

        init {
            for (br in breaks) {
                myMaxLabelWidth = max(myMaxLabelWidth, LABEL_SPEC.width(br.label.length))
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
            return DoubleVector(myMaxLabelWidth, LABEL_SPEC.height())
        }
    }

    companion object {
        fun horizontal(title: String, breaks: List<LegendBreak>, keySize: DoubleVector): LegendComponentLayout {
            return MyHorizontal(
                title,
                breaks,
                keySize
            )
        }

        fun horizontalMultiRow(title: String, breaks: List<LegendBreak>, keySize: DoubleVector): LegendComponentLayout {
            return MyHorizontalMultiRow(
                title,
                breaks,
                keySize
            )
        }

        fun vertical(title: String, breaks: List<LegendBreak>, keySize: DoubleVector): LegendComponentLayout {
            return MyVertical(
                title,
                breaks,
                keySize
            )
        }
    }
}
