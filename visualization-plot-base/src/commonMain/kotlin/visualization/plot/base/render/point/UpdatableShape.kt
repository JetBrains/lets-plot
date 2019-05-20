package jetbrains.datalore.visualization.plot.base.render.point

import jetbrains.datalore.base.values.Color

interface UpdatableShape {
    fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double)
}
