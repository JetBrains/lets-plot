package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.interact.GeomTarget
import jetbrains.datalore.visualization.plot.base.interact.HitShape
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint
import jetbrains.datalore.visualization.plot.builder.interact.loc.TargetPrototype.Companion.createTipLayoutHint

class TestingGeomTargetBuilder(private var myTargetHitCoord: DoubleVector) {

    private var myHintShape: HitShape = HitShape.point(DoubleVector.ZERO, 0.0)
    private val myAesTipLayoutHints: MutableMap<Aes<*>, TipLayoutHint> = HashMap()
    private var myFill = Color.TRANSPARENT

    fun withPointHitShape(coord: DoubleVector, radius: Double): TestingGeomTargetBuilder {
        myHintShape = HitShape.point(coord, radius)
        return this
    }

    fun withPathHitShape(): TestingGeomTargetBuilder {
        myHintShape = HitShape.path(emptyList(), false)
        return this
    }

    fun withPolygonHitShape(cursorCoord: DoubleVector): TestingGeomTargetBuilder {
        myTargetHitCoord = cursorCoord
        myHintShape = HitShape.path(emptyList(), true)
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
