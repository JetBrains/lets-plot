package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.base.observable.collections.Collections

class ScaleBreaks(domainValues: List<Double>, transformValues: List<Double>, labels: List<String>) {
    val domainValues: List<Double> = Collections.unmodifiableList(ArrayList(domainValues))
    val transformValues: List<Double> = Collections.unmodifiableList(ArrayList(transformValues))
    val labels: List<String> = Collections.unmodifiableList(ArrayList(labels))
}
