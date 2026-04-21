/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.TargetPrototype.Companion.createTooltipHint

class TestingGeomTargetBuilder(private var myTargetHitCoord: DoubleVector) {

    private var myHintShape: HitShape = HitShape.point(DoubleVector.ZERO, 0.0)
    private val myAesTooltipHint: MutableMap<Aes<*>, TooltipHint> = HashMap()
    private var myFill = Color.TRANSPARENT

    fun withPointHitShape(coord: DoubleVector, radius: Double): TestingGeomTargetBuilder {
        myHintShape = HitShape.point(coord, radius)
        return this
    }

    fun withPathHitShape(): TestingGeomTargetBuilder {
        myHintShape = HitShape.path(emptyList())
        return this
    }

    fun withPolygonHitShape(cursorCoord: DoubleVector): TestingGeomTargetBuilder {
        myTargetHitCoord = cursorCoord
        myHintShape = HitShape.polygon(emptyList())
        return this
    }

    fun withRectHitShape(rect: DoubleRectangle): TestingGeomTargetBuilder {
        myHintShape = HitShape.rect(rect)
        return this
    }

    fun withLayoutHint(
        aes: Aes<*>,
        layoutHint: TooltipHint
    ): TestingGeomTargetBuilder {
        myAesTooltipHint[aes] = layoutHint
        return this
    }

    fun build(): GeomTarget {

        fun detectTooltipHint(kind: HitShape.Kind): TooltipHint.Placement {
            return when (kind) {
                HitShape.Kind.POINT -> TooltipHint.Placement.VERTICAL
                HitShape.Kind.RECT -> TooltipHint.Placement.HORIZONTAL
                HitShape.Kind.POLYGON -> TooltipHint.Placement.CURSOR
                HitShape.Kind.PATH -> TooltipHint.Placement.HORIZONTAL
            }
        }

        return GeomTarget(
            IGNORED_HIT_INDEX,
            createTooltipHint(
                myTargetHitCoord,
                myHintShape.kind,
                detectTooltipHint(myHintShape.kind),
                TooltipHint.StemLength.NORMAL,
                fillColor = myFill,
                markerColors = emptyList(),
                objectRadius = if (myHintShape.kind == HitShape.Kind.RECT) {
                    myHintShape.rect.width / 2
                } else {
                    0.0
                }
            ),
            myAesTooltipHint
        )
    }

    companion object {
        private const val IGNORED_HIT_INDEX = 1
    }
}
