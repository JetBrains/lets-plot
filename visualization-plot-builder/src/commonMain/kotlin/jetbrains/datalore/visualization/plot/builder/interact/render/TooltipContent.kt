package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.values.Color

internal class TooltipContent(val text: List<String>, val fill: Color, val fontSize: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TooltipContent

        if (text != other.text) return false
        if (fill != other.fill) return false
        if (fontSize != other.fontSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + fill.hashCode()
        result = 31 * result + fontSize.hashCode()
        return result
    }
}
