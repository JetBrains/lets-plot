/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.HitShape
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.interact.loc.TargetPrototype.Companion.createTipLayoutHint

class TestingGeomTargetBuilder(private var myTargetHitCoord: DoubleVector) {

    private var myHintShape: HitShape = HitShape.point(DoubleVector.ZERO, 0.0)
    private val myAesTipLayoutHints: MutableMap<Aes<*>, TipLayoutHint> = HashMap()
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

    fun withLayoutHint(aes: Aes<*>, layoutHint: TipLayoutHint): TestingGeomTargetBuilder {
        myAesTipLayoutHints[aes] = layoutHint
        return this
    }

    fun withFill(fill: Color): TestingGeomTargetBuilder {
        myFill = fill
        return this
    }

    fun build(): GeomTarget {

       fun detectTipLayoutHint(kind : HitShape.Kind) : TipLayoutHint.Kind {
            return when (kind) {
                HitShape.Kind.POINT -> TipLayoutHint.Kind.VERTICAL_TOOLTIP
                HitShape.Kind.RECT -> TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                HitShape.Kind.POLYGON -> TipLayoutHint.Kind.CURSOR_TOOLTIP
                HitShape.Kind.PATH -> TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                else -> error("Unkown hint shape kind")
            }
        }

        return GeomTarget(
            IGNORED_HIT_INDEX,
            createTipLayoutHint(
                myTargetHitCoord,
                myHintShape.kind,
                detectTipLayoutHint(myHintShape.kind),
                TipLayoutHint.StemLength.NORMAL,
                fillColor = myFill,
                markerColors = emptyList(),
                objectRadius = if (myHintShape.kind == HitShape.Kind.RECT) {
                    myHintShape.rect.width / 2
                } else {
                    0.0
                }
            ),
            myAesTipLayoutHints
        )
    }

    companion object {
        private const val IGNORED_HIT_INDEX = 1
    }
}
