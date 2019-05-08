package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTarget
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams
import jetbrains.datalore.visualization.plot.gog.core.event3.HitShape
import jetbrains.datalore.visualization.plot.gog.core.event3.HitShape.Kind.*
import jetbrains.datalore.visualization.plot.gog.core.event3.TipLayoutHint

class GeomTargetPrototype(internal val hitShape: HitShape, internal val indexMapper: (Int) -> Int, private val myTooltipParams: TooltipParams) {

    internal fun crateGeomTarget(hitCoord: DoubleVector, hitIndex: Int): GeomTarget {
        return GeomTarget(
                hitIndex,
                createTipLayoutHint(hitCoord, hitShape, myTooltipParams.getColor()),
                myTooltipParams.getTipLayoutHints()
        )
    }

    companion object {
        internal fun createTipLayoutHint(hitCoord: DoubleVector, hitShape: HitShape, fill: Color): TipLayoutHint {
            return when (hitShape.kind) {
                RECT -> {
                    val radius = hitShape.rect.width / 2
                    TipLayoutHint.horizontalTooltip(hitCoord, radius, fill)
                }

                POINT -> TipLayoutHint.verticalTooltip(hitCoord, hitShape.point.radius, fill)

                PATH -> TipLayoutHint.horizontalTooltip(hitCoord, 0.0, fill)

                POLYGON -> TipLayoutHint.cursorTooltip(hitCoord, fill)
            }
        }
    }
}
