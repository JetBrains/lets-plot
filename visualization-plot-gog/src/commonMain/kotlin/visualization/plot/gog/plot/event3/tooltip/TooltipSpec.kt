package jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.event3.TipLayoutHint
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList

class TooltipSpec(val layoutHint: TipLayoutHint, lines: List<String>, val fill: Color) {
    val lines: List<String>

    init {
        this.lines = unmodifiableList(ArrayList(lines))
    }
}
