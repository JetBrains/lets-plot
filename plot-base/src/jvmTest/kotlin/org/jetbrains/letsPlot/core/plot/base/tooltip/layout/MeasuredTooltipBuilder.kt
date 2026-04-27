/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.LayoutManager.MeasuredTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.TooltipLayoutTestBase.Companion.makeText

internal class MeasuredTooltipBuilder private constructor(
    private val myLayoutHint: Placement,
    private val myCoord: DoubleVector
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
            TooltipModel(
                tooltipHint = hint,
                title = null,
                lines = makeText(myText!!).map(TooltipModel.Line.Companion::withValue),
                fill = myFillColor!!,
                markerColors = emptyList(),
                isSide = true,
                anchor = myAnchor
            ),
            mySize!!, strokeWidth = 0.0
        )
    }

    private fun createHint(): TooltipHint {
        return when (myLayoutHint) {
            Placement.ROTATED -> TooltipHint.rotatedTooltip(myCoord, myObjectRadius!!, myFillColor)
            Placement.VERTICAL -> TooltipHint.verticalTooltip(myCoord, myObjectRadius!!)
            Placement.HORIZONTAL -> TooltipHint.horizontalTooltip(myCoord, myObjectRadius!!)
            Placement.CURSOR -> TooltipHint.cursorTooltip(myCoord)
            Placement.X_AXIS -> TooltipHint.xAxisTooltip(myCoord, fillColor = myFillColor)
            Placement.Y_AXIS -> TooltipHint.yAxisTooltip(myCoord, fillColor = myFillColor)
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

        fun defaultTipSize(w: Number, h: Number): MeasuredTooltipBuilderFactory {
            return defaultTipSize(DoubleVector(w, h))
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

        fun cursor(key: String, coord: DoubleVector): MeasuredTooltipBuilder {
            return setDefaults(cursorTooltip(coord).text(key))
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
            return MeasuredTooltipBuilder(Placement.ROTATED, coord)
        }

        private fun verticalTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Placement.VERTICAL, coord)
        }

        private fun horizontalTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Placement.HORIZONTAL, coord)
        }

        private fun cursorTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Placement.CURSOR, coord)
        }

        private fun xAxisTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Placement.X_AXIS, coord)
        }

        private fun yAxisTooltip(coord: DoubleVector): MeasuredTooltipBuilder {
            return MeasuredTooltipBuilder(Placement.Y_AXIS, coord)
        }
    }
}
