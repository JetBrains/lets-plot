package jetbrains.datalore.visualization.plot.builder.layout.axis

interface AxisBreaksProvider {
    val isFixedBreaks: Boolean

    val fixedBreaks: GuideBreaks

    fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks
}
