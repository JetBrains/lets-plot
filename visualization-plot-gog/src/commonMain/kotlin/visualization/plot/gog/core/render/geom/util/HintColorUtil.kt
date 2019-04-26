package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors.solid
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

object HintColorUtil {
    fun fromColor(p: DataPointAesthetics): Color {
        return fromColorValue(p.color()!!, p.alpha()!!)
    }

    fun fromFill(p: DataPointAesthetics): Color {
        return fromColorValue(p.fill()!!, p.alpha()!!)
    }

    fun fromColorValue(color: Color, alpha: Double): Color {
        val intAlpha = (255 * alpha).toInt()
        return if (solid(color)) {
            color.changeAlpha(intAlpha)
        } else color

    }
}
