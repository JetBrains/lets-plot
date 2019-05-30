package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint

class TooltipSpec(val layoutHint: TipLayoutHint, lines: List<String>, val fill: Color) {
    val lines: List<String> = ArrayList(lines)

    override fun toString(): String {
        return "TooltipSpec($layoutHint, lines=$lines)"
    }
}
