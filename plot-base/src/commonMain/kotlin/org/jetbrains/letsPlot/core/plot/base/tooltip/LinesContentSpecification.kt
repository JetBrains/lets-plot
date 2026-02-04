package org.jetbrains.letsPlot.core.plot.base.tooltip

data class LinesContentSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<LinePattern>?,
    val titleLine: LinePattern?
)