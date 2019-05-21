package jetbrains.datalore.visualization.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.builder.guide.LegendDirection
import jetbrains.datalore.visualization.plot.builder.guide.LegendJustification
import jetbrains.datalore.visualization.plot.builder.guide.LegendPosition

interface LegendTheme {
    fun keySize(): Double

    /**
     * extra space added around legend (px, no support for ggplot 'units')
     */
    fun margin(): Double

    /**
     * space around legend content (px)
     * this is not part of ggplot specs
     */
    fun padding(): Double

    fun position(): LegendPosition

    fun justification(): LegendJustification

    fun direction(): LegendDirection

    fun backgroundFill(): Color
}
