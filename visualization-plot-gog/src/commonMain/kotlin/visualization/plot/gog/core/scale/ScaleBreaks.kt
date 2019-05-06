package jetbrains.datalore.visualization.plot.gog.core.scale

class ScaleBreaks(domainValues: List<Double>, transformValues: List<Double>, labels: List<String>) {
    val domainValues: List<Double> = ArrayList(domainValues)
    val transformValues: List<Double> = ArrayList(transformValues)
    val labels: List<String> = ArrayList(labels)
}
