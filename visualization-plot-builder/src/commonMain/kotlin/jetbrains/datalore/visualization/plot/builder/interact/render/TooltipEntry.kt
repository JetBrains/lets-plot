package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipOrientation

internal class TooltipEntry(val tooltipContent: TooltipContent,
                            val tooltipCoord: DoubleVector,
                            val stemCoord: DoubleVector,
                            val orientation: TooltipOrientation) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TooltipEntry

        if (tooltipContent != other.tooltipContent) return false
        if (tooltipCoord != other.tooltipCoord) return false
        if (stemCoord != other.stemCoord) return false
        if (orientation != other.orientation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tooltipContent.hashCode()
        result = 31 * result + tooltipCoord.hashCode()
        result = 31 * result + stemCoord.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }
}
