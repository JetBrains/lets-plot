package jetbrains.datalore.visualization.plot.gog.plot.layout.axis

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList

class GuideBreaks(domainValues: List<*>, transformedValues: List<Double>, labels: List<String>) {
    val domainValues: List<*>
    val transformedValues: List<Double>
    val labels: List<String>

    val isEmpty: Boolean
        get() = transformedValues.isEmpty()

    init {
        checkArgument(
                domainValues.size == transformedValues.size,
                "Scale breaks size: " + domainValues.size + " transformed size: " + transformedValues.size + " but expected to be the same"
        )
        checkArgument(
                domainValues.size == labels.size,
                "Scale breaks size: " + domainValues.size + " labels size: " + labels.size + " but expected to be the same"
        )
        this.domainValues = unmodifiableList(ArrayList(domainValues))
        this.transformedValues = unmodifiableList(ArrayList(transformedValues))
        this.labels = unmodifiableList(ArrayList(labels))
    }

    fun size(): Int {
        return transformedValues.size
    }
}
