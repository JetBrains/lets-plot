/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

class GuideBreaks(domainValues: List<Any>, transformedValues: List<Double>, labels: List<String>) {
    val domainValues: List<Any>
    val transformedValues: List<Double>
    val labels: List<String>

    val isEmpty: Boolean
        get() = transformedValues.isEmpty()

    init {
        require(domainValues.size == transformedValues.size)
        { "Scale breaks size: " + domainValues.size + " transformed size: " + transformedValues.size + " but expected to be the same" }
        require(domainValues.size == labels.size)
        { "Scale breaks size: " + domainValues.size + " labels size: " + labels.size + " but expected to be the same" }
        this.domainValues = ArrayList(domainValues)
        this.transformedValues = ArrayList(transformedValues)
        this.labels = ArrayList(labels)
    }

    fun size(): Int {
        return transformedValues.size
    }
}
