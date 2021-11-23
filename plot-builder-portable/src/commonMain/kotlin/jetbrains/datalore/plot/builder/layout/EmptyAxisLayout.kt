/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.coord.CoordProvider

class EmptyAxisLayout private constructor(
    xDomain: ClosedRange<Double>,
    yDomain: ClosedRange<Double>,
    private val myOrientation: jetbrains.datalore.plot.builder.guide.Orientation
) :
    AxisLayout {

    private val myAxisDomain: ClosedRange<Double>

    init {
        myAxisDomain = if (myOrientation.isHorizontal) xDomain else yDomain
    }

    override fun initialThickness(): Double {
        return 0.0
    }

    override fun doLayout(
        displaySize: DoubleVector,
        maxTickLabelsBoundsStretched: DoubleRectangle?,
        coordProvider: CoordProvider
    ): AxisLayoutInfo {
        val axisLength = if (myOrientation.isHorizontal) displaySize.x else displaySize.y
        // relative to axis component
        val tickLabelsBounds = if (myOrientation.isHorizontal) {
            DoubleRectangle(0.0, 0.0, axisLength, 0.0)
        } else {
            DoubleRectangle(0.0, 0.0, 0.0, axisLength)
        }

        val builder = AxisLayoutInfo.Builder()
            .axisBreaks(ScaleBreaks.EMPTY)
            .axisLength(axisLength)
            .orientation(myOrientation)
            .axisDomain(myAxisDomain)
            .tickLabelsBounds(tickLabelsBounds)

        return builder.build()
    }

    companion object {
        fun bottom(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>): AxisLayout {
            return EmptyAxisLayout(
                xDomain,
                yDomain,
                jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM
            )
        }

        fun left(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>): AxisLayout {
            return EmptyAxisLayout(
                xDomain,
                yDomain,
                jetbrains.datalore.plot.builder.guide.Orientation.LEFT
            )
        }
    }
}
