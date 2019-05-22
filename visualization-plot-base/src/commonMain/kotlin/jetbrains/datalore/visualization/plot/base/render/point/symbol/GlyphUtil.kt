package jetbrains.datalore.visualization.plot.base.render.point.symbol

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.visualization.base.svg.SvgPathData
import jetbrains.datalore.visualization.base.svg.SvgPathDataBuilder

internal object GlyphUtil {
    fun buildPathData(xs: Collection<Double>, ys: Collection<Double>): SvgPathData {
        Preconditions.checkArgument(xs.size == ys.size, "Sizes of X/Y collections must be equal")

        if (xs.isEmpty()) {
            return SvgPathData.EMPTY
        }

        val builder = SvgPathDataBuilder(true)
                .moveTo(Iterables[xs, 0], Iterables[ys, 0])
                .interpolatePoints(xs, ys, SvgPathDataBuilder.Interpolation.LINEAR)
                .closePath()

        return builder.build()
    }
}
