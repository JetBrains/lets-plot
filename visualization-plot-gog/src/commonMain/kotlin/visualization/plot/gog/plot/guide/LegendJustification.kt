package jetbrains.datalore.visualization.plot.gog.plot.guide

/**
 * anchor point for positioning legend inside plot ("center" or two-element numeric vector)
 */
class LegendJustification(val x: Double, val y: Double) {
    companion object {
        val CENTER = LegendJustification(0.5, 0.5)
    }
}
