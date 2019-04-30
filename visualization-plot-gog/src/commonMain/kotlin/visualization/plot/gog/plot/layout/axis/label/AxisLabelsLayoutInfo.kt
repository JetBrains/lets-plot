package jetbrains.datalore.visualization.plot.gog.plot.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.GuideBreaks
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList

class AxisLabelsLayoutInfo private constructor(b: Builder) {
    val breaks: GuideBreaks?
    val bounds: DoubleRectangle?
    val smallFont: Boolean
    val labelAdditionalOffsets: List<DoubleVector>?
    val labelHorizontalAnchor: TextLabel.HorizontalAnchor?
    val labelVerticalAnchor: TextLabel.VerticalAnchor?
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
            unmodifiableList(ArrayList(b.myLabelAdditionalOffsets!!))
        this.labelHorizontalAnchor = b.myLabelHorizontalAnchor
        this.labelVerticalAnchor = b.myLabelVerticalAnchor
        this.labelRotationAngle = b.myLabelRotationAngle
    }

    class Builder {
        internal var myBreaks: GuideBreaks? = null
        internal var myBounds: DoubleRectangle? = null
        internal var mySmallFont: Boolean = false
        internal var myOverlap: Boolean = false
        internal var myLabelAdditionalOffsets: List<DoubleVector>? = null
        internal var myLabelHorizontalAnchor: TextLabel.HorizontalAnchor? = null
        internal var myLabelVerticalAnchor: TextLabel.VerticalAnchor? = null
        internal var myLabelRotationAngle = 0.0

        fun breaks(breaks: GuideBreaks): Builder {
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

        fun labelAdditionalOffsets(l: List<DoubleVector>): Builder {
            myLabelAdditionalOffsets = l
            return this
        }

        fun labelHorizontalAnchor(anchor: TextLabel.HorizontalAnchor): Builder {
            myLabelHorizontalAnchor = anchor
            return this
        }

        fun labelVerticalAnchor(anchor: TextLabel.VerticalAnchor): Builder {
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
