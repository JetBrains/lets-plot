/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation

class AxisLayoutInfo private constructor(b: Builder) {
    val axisBreaks: ScaleBreaks
    val axisLength: Double
    val orientation: Orientation
    val axisDomain: DoubleSpan

    val tickLabelsBounds: DoubleRectangle?
    val tickLabelRotationAngle: Double
    val tickLabelHorizontalAnchor: Text.HorizontalAnchor?    // optional
    val tickLabelVerticalAnchor: Text.VerticalAnchor?        // optional
    val tickLabelAdditionalOffsets: List<DoubleVector>?           // optional
    val tickLabelSmallFont: Boolean
    private val tickLabelsBoundsMax: DoubleRectangle?                     // debug

    init {
        require(b.myAxisBreaks != null)
        require(b.myOrientation != null)
        require(b.myTickLabelsBounds != null)
        require(b.myAxisDomain != null)

        this.axisBreaks = b.myAxisBreaks!!
        this.axisLength = b.myAxisLength
        this.orientation = b.myOrientation!!
        this.axisDomain = b.myAxisDomain!!

        this.tickLabelsBounds = b.myTickLabelsBounds
        this.tickLabelRotationAngle = b.myTickLabelRotationAngle
        this.tickLabelHorizontalAnchor = b.myLabelHorizontalAnchor
        this.tickLabelVerticalAnchor = b.myLabelVerticalAnchor
        this.tickLabelAdditionalOffsets = b.myLabelAdditionalOffsets
        this.tickLabelSmallFont = b.myTickLabelSmallFont
        this.tickLabelsBoundsMax = b.myMaxTickLabelsBounds
    }

    fun withAxisLength(axisLength: Double): Builder {
        //check(axisDomain != null)

        val b = Builder()
        b.myAxisBreaks = axisBreaks
        b.myAxisLength = axisLength

        b.myOrientation = this.orientation
        b.myAxisDomain = this.axisDomain

        b.myTickLabelsBounds = this.tickLabelsBounds
        b.myTickLabelRotationAngle = this.tickLabelRotationAngle
        b.myLabelHorizontalAnchor = this.tickLabelHorizontalAnchor
        b.myLabelVerticalAnchor = this.tickLabelVerticalAnchor
        b.myLabelAdditionalOffsets = this.tickLabelAdditionalOffsets
        b.myTickLabelSmallFont = this.tickLabelSmallFont
        b.myMaxTickLabelsBounds = this.tickLabelsBoundsMax
        return b
    }

    fun axisBounds(): DoubleRectangle {
        return tickLabelsBounds!!.union(DoubleRectangle(0.0, 0.0, 0.0, 0.0))
    }

    class Builder {
        var myAxisLength: Double = 0.0
        var myOrientation: Orientation? = null
        var myAxisDomain: DoubleSpan? = null
        var myMaxTickLabelsBounds: DoubleRectangle? = null
        var myTickLabelSmallFont = false
        var myLabelAdditionalOffsets: List<DoubleVector>? = null
        var myLabelHorizontalAnchor: Text.HorizontalAnchor? = null
        var myLabelVerticalAnchor: Text.VerticalAnchor? = null
        var myTickLabelRotationAngle = 0.0
        var myTickLabelsBounds: DoubleRectangle? = null
        var myAxisBreaks: ScaleBreaks? = null

        fun build(): AxisLayoutInfo {
            return AxisLayoutInfo(this)
        }

        fun axisLength(d: Double): Builder {
            myAxisLength = d
            return this
        }

        fun orientation(o: Orientation): Builder {
            myOrientation = o
            return this
        }

        fun axisDomain(r: DoubleSpan): Builder {
            myAxisDomain = r
            return this
        }

        fun tickLabelsBoundsMax(r: DoubleRectangle?): Builder {
            myMaxTickLabelsBounds = r
            return this
        }

        fun tickLabelSmallFont(b: Boolean): Builder {
            myTickLabelSmallFont = b
            return this
        }

        fun tickLabelAdditionalOffsets(labelAdditionalOffsets: List<DoubleVector>?): Builder {
            myLabelAdditionalOffsets = labelAdditionalOffsets
            return this
        }

        fun tickLabelHorizontalAnchor(labelHorizontalAnchor: Text.HorizontalAnchor?): Builder {
            myLabelHorizontalAnchor = labelHorizontalAnchor
            return this
        }

        fun tickLabelVerticalAnchor(labelVerticalAnchor: Text.VerticalAnchor?): Builder {
            myLabelVerticalAnchor = labelVerticalAnchor
            return this
        }

        fun tickLabelRotationAngle(rotationAngle: Double): Builder {
            myTickLabelRotationAngle = rotationAngle
            return this
        }

        fun tickLabelsBounds(rectangle: DoubleRectangle?): Builder {
            myTickLabelsBounds = rectangle
            return this
        }

        fun axisBreaks(breaks: ScaleBreaks?): Builder {
            myAxisBreaks = breaks
            return this
        }
    }
}
