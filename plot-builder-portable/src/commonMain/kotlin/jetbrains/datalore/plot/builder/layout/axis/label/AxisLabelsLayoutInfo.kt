/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks

class AxisLabelsLayoutInfo private constructor(b: Builder) {
    val breaks: ScaleBreaks?
    val bounds: DoubleRectangle?
    val smallFont: Boolean
    val labelAdditionalOffsets: List<DoubleVector>?
    val labelHorizontalAnchor: Text.HorizontalAnchor?
    val labelVerticalAnchor: Text.VerticalAnchor?
    val labelRotationAngle: Double
    internal val isOverlap: Boolean


    init {
        this.breaks = b.myBreaks
        this.smallFont = b.mySmallFont
        this.bounds = b.myBounds
        this.isOverlap = b.myOverlap
        this.labelAdditionalOffsets = if (b.myLabelAdditionalOffsets == null)
            null
        else
            ArrayList(b.myLabelAdditionalOffsets!!)
        this.labelHorizontalAnchor = b.myLabelHorizontalAnchor
        this.labelVerticalAnchor = b.myLabelVerticalAnchor
        this.labelRotationAngle = b.myLabelRotationAngle
    }

    class Builder {
        internal var myBreaks: ScaleBreaks? = null
        internal var myBounds: DoubleRectangle? = null
        internal var mySmallFont: Boolean = false
        internal var myOverlap: Boolean = false
        internal var myLabelAdditionalOffsets: List<DoubleVector>? = null
        internal var myLabelHorizontalAnchor: Text.HorizontalAnchor? = null
        internal var myLabelVerticalAnchor: Text.VerticalAnchor? = null
        internal var myLabelRotationAngle = 0.0

        fun breaks(breaks: ScaleBreaks): Builder {
            myBreaks = breaks
            return this
        }

        fun bounds(bounds: DoubleRectangle): Builder {
            myBounds = bounds
            return this
        }

        fun smallFont(b: Boolean): Builder {
            mySmallFont = b
            return this
        }

        fun overlap(b: Boolean): Builder {
            myOverlap = b
            return this
        }

        fun labelAdditionalOffsets(l: List<DoubleVector>?): Builder {
            myLabelAdditionalOffsets = l
            return this
        }

        fun labelHorizontalAnchor(anchor: Text.HorizontalAnchor): Builder {
            myLabelHorizontalAnchor = anchor
            return this
        }

        fun labelVerticalAnchor(anchor: Text.VerticalAnchor): Builder {
            myLabelVerticalAnchor = anchor
            return this
        }

        fun labelRotationAngle(angle: Double): Builder {
            myLabelRotationAngle = angle
            return this
        }

        fun build(): AxisLabelsLayoutInfo {
            return AxisLabelsLayoutInfo(this)
        }
    }
}
