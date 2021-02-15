/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.TreeMap
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import kotlin.math.abs

internal class DiscreteScale<T> : AbstractScale<Any, T> {
    private val myNumberByDomainValue = LinkedHashMap<Any, Double>()
    private var myDomainValueByNumber: TreeMap<Double, Any> = TreeMap()
    private val myDomainLimits: MutableList<Any> = ArrayList<Any>()

    override var breaks: List<Any>
        get() {
            val breaks = super.breaks
            if (!hasDomainLimits()) {
                return breaks
            }

            // filter domain and preserve the order defined by limits
            val intersect = myDomainLimits.intersect(breaks)
            return myDomainLimits.filter { it in intersect }
        }
        set(breaks) {
            super.breaks = breaks
        }

    override val labels: List<String>
        get() {
            val labels = super.labels
            if (!hasDomainLimits()) {
                return labels
            }
            if (labels.isEmpty()) {
                return labels
            }

            val breaks = super.breaks
            val labelByValue = HashMap<Any?, String>()
            for ((i, v) in breaks.withIndex()) {
                labelByValue[v] = labels[i % labels.size]
            }

            // filter domain and preserve the order defined by limits
            val intersect = myDomainLimits.intersect(breaks)
            return myDomainLimits.filter { it in intersect }
                .map { labelByValue[it]!! }
        }

    override val defaultTransform: Transform
        get() = object : Transform {
            override fun apply(rawData: List<*>): List<Double?> {
                val l = ArrayList<Double?>()
                for (o in rawData) {
                    l.add(asNumber(o))
                }
                return l
            }

            override fun applyInverse(v: Double?): Any? {
                return fromNumber(v)
            }
        }

    override val domainLimits: ClosedRange<Double>?
        get() = throw IllegalStateException("Not applicable to scale with discrete domain '$name'")

    constructor(name: String, domainValues: Collection<Any?>, mapper: ((Double?) -> T?)) : super(name, mapper) {
        updateDomain(domainValues, emptyList())

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.6
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        updateDomain(b.myDomainValues, b.myDomainLimits)
    }

    private fun updateDomain(domainValues: Collection<Any?>, domainLimits: List<Any>) {
        val effectiveDomain = ArrayList<Any?>()
        if (domainLimits.isEmpty()) {
            effectiveDomain.addAll(domainValues)
        } else {
            effectiveDomain.addAll(domainLimits.intersect(domainValues))
        }

        if (!hasBreaks()) {
            breaks = effectiveDomain.mapNotNull { it }
        }

        myDomainLimits.clear()
        myDomainLimits.addAll(domainLimits)

        myNumberByDomainValue.clear()
        myNumberByDomainValue.putAll(
            MapperUtil.mapDiscreteDomainValuesToNumbers(
                effectiveDomain
            )
        )
        myDomainValueByNumber = TreeMap()
        for (domainValue in myNumberByDomainValue.keys) {
            myDomainValueByNumber.put(myNumberByDomainValue[domainValue]!!, domainValue)
        }
    }

    override fun hasDomainLimits(): Boolean {
        return myDomainLimits.isNotEmpty()
    }

    override fun isInDomainLimits(v: Any): Boolean {
        return if (hasDomainLimits()) {
            myDomainLimits.contains(v) && myNumberByDomainValue.containsKey(v)
        } else myNumberByDomainValue.containsKey(v)
    }

    override fun asNumber(input: Any?): Double? {
        if (input == null) {
            return null
        }
        if (myNumberByDomainValue.containsKey(input)) {
            return myNumberByDomainValue[input]
        }
        throw IllegalArgumentException(
            "'" + name
                    + "' : value {" + input + "} is not in scale domain: " + myNumberByDomainValue
        )
    }

    private fun fromNumber(v: Double?): Any? {
        if (v == null) {
            return null
        }

        if (myDomainValueByNumber.containsKey(v)) {
            return myDomainValueByNumber[v]
        }
        // look-up the closest key (number)
        val ceilingKey = myDomainValueByNumber.ceilingKey(v)
        val floorKey = myDomainValueByNumber.floorKey(v)
        var keyNumber: Double? = null
        if (ceilingKey != null || floorKey != null) {
            keyNumber = when {
                ceilingKey == null -> floorKey
                floorKey == null -> ceilingKey
                else -> {
                    val ceilingDist = abs(ceilingKey - v)
                    val floorDist = abs(floorKey - v)
                    if (ceilingDist < floorDist) ceilingKey else floorKey
                }
            }
        }
        return if (keyNumber != null) myDomainValueByNumber[keyNumber] else null
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }

    private class MyBuilder<T> internal constructor(scale: DiscreteScale<T>) : AbstractBuilder<Any, T>(scale) {
        internal val myDomainValues: Collection<Any>
        private var myNewBreaks: List<Any>? = null
        internal var myDomainLimits: List<Any> = emptyList()

        init {
            myDomainValues = scale.myNumberByDomainValue.keys  // ordered (from LinkedHashMap)
            myDomainLimits = scale.myDomainLimits
        }

        override fun lowerLimit(v: Double): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun upperLimit(v: Double): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun limits(domainValues: List<Any>): Scale.Builder<T> {
            myDomainLimits = domainValues
            return this
        }

        override fun breaks(l: List<Any>): Scale.Builder<T> {
            myNewBreaks = l
            // don't call super!
            return this
        }

        override fun continuousTransform(v: Transform): Scale.Builder<T> {
            // ignore
            return this
        }

        override fun build(): Scale<T> {
            val scale = DiscreteScale(this)
            if (myNewBreaks == null) {
                return scale
            }

            // breaks was updated
            scale.breaks = myNewBreaks!!

            // re-validate domain values
            if (!myDomainValues.containsAll(myNewBreaks!!)) {
                // extend domain to make sure all breaks are visible
                val newDomainValues = LinkedHashSet(myNewBreaks!!)
                newDomainValues.addAll(myDomainValues)
                scale.updateDomain(newDomainValues, scale.myDomainLimits)
            }
            return scale
        }
    }
}
