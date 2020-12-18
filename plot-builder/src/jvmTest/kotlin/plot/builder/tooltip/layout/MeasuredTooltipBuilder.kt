/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.plot.builder.tooltip.layout.TooltipLayoutTestBase.Companion.makeText

internal class MeasuredTooltipBuilder private constructor(private val myLayoutHint: Kind, private val myCoord: DoubleVector?) {
    private var mySize: DoubleVector? = null
    private var myObjectRadius: Double? = null
    private var myText: String? = null
    private var myFill: Color? = null

    fun size(v: DoubleVector): MeasuredTooltipBuilder {
        mySize = v
        return this
    }

    fun size(width: Double, height: Double): MeasuredTooltipBuilder {
        return size(DoubleVector(width, height))
    }

    fun objectRadius(v: Double): MeasuredTooltipBuilder {
        myObjectRadius = v
        return this
    }

    fun text(v: String): MeasuredTooltipBuilder {
        myText = v
        return this
    }

    private fun fill(v: Color): MeasuredTooltipBuilder {
        myFill = v
        return this
    }

    fun buildTooltip(anchor: TooltipAnchor? = null): MeasuredTooltip {
        val hint = createHint()
        return MeasuredTooltip(
            TooltipSpec(
            layoutHint = hint,
            lines = makeText(myText!!).map(TooltipSpec.Line.Companion::withValue),
            fill = myFill!!,
            isOutlier = true,
            anchor = anchor
        ), mySize!!, TooltipBox())
    }

    private fun createHint(): TipLayoutHint {
        return when (myLayoutHint) {
            Kind.VERTICAL_TOOLTIP -> TipLayoutHint.verticalTooltip(myCoord, myObjectRadius!!, myFill)

            Kind.HORIZONTAL_TOOLTIP -> TipLayoutHint.horizontalTooltip(myCoord, myObjectRadius!!, myFill)

            Kind.CURSOR_TOOLTIP -> TipLayoutHint.cursorTooltip(myCoord, myFill)

            Kind.X_AXIS_TOOLTIP -> TipLayoutHint.xAxisTooltip(myCoord, myFill)

            Kind.Y_AXIS_TOOLTIP -> TipLayoutHint.yAxisTooltip(myCoord, myFill)

            else -> throw IllegalStateException("Unknown layout hint")
        }
    }

    internal class MeasuredTooltipBuilderFactory {
        private var myDefaultTooltipSize: DoubleVector? = null
        private var myDefaultObjectRadius: Double? = null
        private var myDefaultTipText: String? = null
        private val myDefaultFill = Color.WHITE

        fun defaultTipSize(v: DoubleVector): MeasuredTooltipBuilderFactory {
            myDefaultTooltipSize = v
            return this
        }

        fun defaultTipSize(x: Double, y: Double): MeasuredTooltipBuilderFactory {
            return defaultTipSize(DoubleVector(x, y))
        }

        fun defaultObjectRadius(v: Double): MeasuredTooltipBuilderFactory {
            myDefaultObjectRadius = v
            return this
        }

        fun defaultTipText(v: String): MeasuredTooltipBuilderFactory {
            myDefaultTipText = v
            return this
        }

        fun vertical(key: String, targetCoord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(verticalTooltip(targetCoord).text(key))
        }

        fun horizontal(key: String, targetCoord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(horizontalTooltip(targetCoord).text(key))
        }

        fun cursor(key: String): MeasuredTooltipBuilder {
            return setDefaults(cursorTooltip().text(key))
        }

        fun xAxisTip(key: String, targetCoord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(xAxisTooltip(targetCoord).text(key))
        }

        fun yAxisTip(key: String, targetCoord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(yAxisTooltip(targetCoord).text(key))
        }

        private fun setDefaults(builder: MeasuredTooltipBuilder): MeasuredTooltipBuilder {
            if (myDefaultObjectRadius != null) {
                builder.objectRadius(myDefaultObjectRadius!!)
            }

            if (myDefaultTipText != null) {
                builder.text(myDefaultTipText!!)
            }

            if (myDefaultTooltipSize != null) {
                builder.size(myDefaultTooltipSize!!)
            }

            builder.fill(myDefaultFill)
            return builder
        }
    }

    companion object {

        private fun verticalTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.VERTICAL_TOOLTIP, coord)
        }

        private fun horizontalTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.HORIZONTAL_TOOLTIP, coord)
        }

        private fun cursorTooltip(): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.CURSOR_TOOLTIP, null)
        }

        private fun xAxisTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.X_AXIS_TOOLTIP, coord)
        }

        private fun yAxisTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.Y_AXIS_TOOLTIP, coord)
        }
    }
}
