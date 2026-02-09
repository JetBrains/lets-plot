package org.jetbrains.letsPlot.core.plot.base.tooltip.text

data class LinesContentSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<LinePattern>?,
    val titleLine: LinePattern?
)