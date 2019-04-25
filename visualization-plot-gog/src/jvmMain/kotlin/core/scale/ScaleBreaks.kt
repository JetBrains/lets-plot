package jetbrains.datalore.visualization.plot.gog.core.scale

import java.util.*

class ScaleBreaks(domainValues: List<Double>, transformValues: List<Double>, labels: List<String>) {
    val domainValues: List<Double>
    val transformValues: List<Double>
    val labels: List<String>

    init {
        this.domainValues = Collections.unmodifiableList(ArrayList(domainValues))
        this.transformValues = Collections.unmodifiableList(ArrayList(transformValues))
        this.labels = Collections.unmodifiableList(ArrayList(labels))
    }
}
