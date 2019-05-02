package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.base.values.Color

interface SvgSlimShape : SvgSlimObject {
    fun setFill(c: Color, alpha: Double)
    fun setStroke(c: Color, alpha: Double)
    fun setStrokeWidth(v: Double)
}
