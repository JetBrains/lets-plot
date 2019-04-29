package jetbrains.datalore.visualization.plot.gog.plot.layout.axis

interface AxisBreaksProvider {
    val isFixedBreaks: Boolean

    val fixedBreaks: GuideBreaks

    fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks
}
