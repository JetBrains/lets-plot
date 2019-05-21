package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.event3.GeomTarget
import jetbrains.datalore.visualization.plot.base.event3.HitShape
import jetbrains.datalore.visualization.plot.base.event3.TipLayoutHint
import jetbrains.datalore.visualization.plot.base.render.Aes

import jetbrains.datalore.visualization.plot.builder.event3.GeomTargetPrototype.Companion.createTipLayoutHint

class GeomTargetBuilder(private var myTargetHitCoord: DoubleVector) {

    private var myHintShape: HitShape = HitShape.point(DoubleVector.ZERO, 0.0)
    private val myAesTipLayoutHints: MutableMap<Aes<*>, TipLayoutHint> = HashMap()
    private var myFill = Color.TRANSPARENT

    fun withPointHitShape(coord: DoubleVector, radius: Double): GeomTargetBuilder {
        myHintShape = HitShape.point(coord, radius)
        return this
    }

    fun withPathHitShape(): GeomTargetBuilder {
        myHintShape = HitShape.path(emptyList(), false)
        return this
    }

    fun withPolygonHitShape(cursorCoord: DoubleVector): GeomTargetBuilder {
        myTargetHitCoord = cursorCoord
        myHintShape = HitShape.path(emptyList(), true)
        return this
    }

    fun withRectHitShape(rect: DoubleRectangle): GeomTargetBuilder {
        myHintShape = HitShape.rect(rect)
        return this
    }

    fun withLayoutHint(aes: Aes<*>, layoutHint: TipLayoutHint): GeomTargetBuilder {
        myAesTipLayoutHints[aes] = layoutHint
        return this
    }

    fun withFill(fill: Color): GeomTargetBuilder {
        myFill = fill
        return this
    }

    fun build(): GeomTarget {
        return GeomTarget(
                IGNORED_HIT_INDEX,
                createTipLayoutHint(myTargetHitCoord, myHintShape, myFill),
                myAesTipLayoutHints
        )
    }

    companion object {
        private const val IGNORED_HIT_INDEX = 1
    }
}
