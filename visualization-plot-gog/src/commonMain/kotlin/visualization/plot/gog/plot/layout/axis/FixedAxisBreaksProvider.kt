package jetbrains.datalore.visualization.plot.gog.plot.layout.axis

class FixedAxisBreaksProvider(domainBreaks: List<*>, transformedBreaks: List<Double>, labels: List<String>) : AxisBreaksProvider {
    override val fixedBreaks: GuideBreaks = GuideBreaks(domainBreaks, transformedBreaks, labels)

    override val isFixedBreaks: Boolean
        get() = true

    override fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks {
        return fixedBreaks
    }
}
