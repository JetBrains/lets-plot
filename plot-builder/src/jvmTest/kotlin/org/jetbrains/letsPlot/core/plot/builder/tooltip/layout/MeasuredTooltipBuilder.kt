/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.TooltipBox
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.TooltipLayoutTestBase.Companion.makeText
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec

internal class MeasuredTooltipBuilder private constructor(
    private val myLayoutHint: Kind,
    private val myCoord: DoubleVector?
) {
    private var mySize: DoubleVector? = null
    private var myObjectRadius: Double? = null
    private var myText: String? = null
    private var myFillColor: Color? = null
    private var myAnchor: TooltipAnchor? = null

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

    private fun fillColor(v: Color): MeasuredTooltipBuilder {
        myFillColor = v
        return this
    }

    fun anchor(v: TooltipAnchor): MeasuredTooltipBuilder {
        myAnchor = v
        return this
    }

    fun buildTooltip(): MeasuredTooltip {
        val hint = createHint()
        return MeasuredTooltip(
            TooltipSpec(
                layoutHint = hint,
                title = null,
                lines = makeText(myText!!).map(TooltipSpec.Line.Companion::withValue),
                fill = myFillColor!!,
                markerColors = emptyList(),
                isSide = true,
                anchor = myAnchor
            ),
            mySize!!, TooltipBox(), strokeWidth = 0.0
        )
    }

    private fun createHint(): TipLayoutHint {
        return when (myLayoutHint) {
            Kind.ROTATED_TOOLTIP -> TipLayoutHint.rotatedTooltip(myCoord, myObjectRadius!!, myFillColor)
            Kind.VERTICAL_TOOLTIP -> TipLayoutHint.verticalTooltip(myCoord, myObjectRadius!!)
            Kind.HORIZONTAL_TOOLTIP -> TipLayoutHint.horizontalTooltip(myCoord, myObjectRadius!!)
            Kind.CURSOR_TOOLTIP -> TipLayoutHint.cursorTooltip(myCoord)
            Kind.X_AXIS_TOOLTIP -> TipLayoutHint.xAxisTooltip(myCoord, fillColor = myFillColor)
            Kind.Y_AXIS_TOOLTIP -> TipLayoutHint.yAxisTooltip(myCoord, fillColor = myFillColor)
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

        fun rotated(key: String, targetCoord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(rotatedTooltip(targetCoord).text(key))
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

            builder.fillColor(myDefaultFill)
            return builder
        }
    }

    companion object {

        private fun rotatedTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Kind.ROTATED_TOOLTIP, coord)
        }

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
