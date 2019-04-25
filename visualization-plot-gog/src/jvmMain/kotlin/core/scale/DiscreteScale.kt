package jetbrains.datalore.visualization.plot.gog.core.scale


import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil

import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.TreeMap

internal class DiscreteScale<T> : AbstractScale<Any, T> {
    private val myNumberByDomainValue = LinkedHashMap<Any, Double>()
    private var myDomainLimits: Set<Any> = emptySet()
    private var myDomainValueByNumber: TreeMap<Double, Any>? = null

    override var breaks: List<Any> = super.breaks
        get() {
            val breaks = super.breaks
            if (!hasDomainLimits()) {
                return breaks
            }
            val indices = SeriesUtil.matchingIndices(breaks, myDomainLimits)
            return SeriesUtil.pickAtIndices(breaks, indices)
        }

    override val labels: MutableList<String>
        get() {
            val labels = super.labels
            if (!hasDomainLimits() || !hasBreaks()) {
                return labels
            }
            if (labels.isEmpty()) {
                return labels
            }

            val indices = SeriesUtil.matchingIndices(breaks, myDomainLimits)
            val labelsFiltered = ArrayList<String>()
            for (i in indices) {
                labelsFiltered.add(labels[i % labels.size])
            }
            return labelsFiltered
        }

    override val defaultTransform: Transform
        get() = object : Transform {
            override fun apply(rawData: List<*>): List<Double> {
                val l = ArrayList<Double>()
                for (o in rawData) {
                    l.add(asNumber(o)!!)
                }
                return l
            }

            override fun applyInverse(v: Double): Any? {
                return fromNumber(v)
            }
        }

    override val domainLimits: ClosedRange<Double>
        get() = throw IllegalStateException("Not applicable to scale with discrete domain '$name'")

    constructor(name: String, domainValues: Collection<Any>, mapper: ((Double) -> T)?) : super(name, mapper) {
        updateDomain(domainValues, emptySet<Any>())

        // see: http://docs.ggplot2.org/current/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.6
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        updateDomain(b.myIndexByValue.keys, b.myDomainLimits)
    }

    private fun updateDomain(domainValues: Collection<Any>, domainLimits: Set<Any>) {
        val effectiveDomain: MutableList<Any>
        if (domainLimits.isEmpty()) {
            effectiveDomain = ArrayList<Any>(domainValues)
        } else {
            effectiveDomain = ArrayList()
            for (domainValue in domainValues) {
                if (domainLimits.contains(domainValue)) {
                    effectiveDomain.add(domainValue)
                }
            }
        }

        if (!hasBreaks()) {
            breaks = effectiveDomain
        }

        myNumberByDomainValue.clear()
        myNumberByDomainValue.putAll(MapperUtil.mapDiscreteDomainValuesToNumbers(effectiveDomain))
        myDomainLimits = HashSet(domainLimits)
        myDomainValueByNumber = null
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
        throw IllegalArgumentException("'" + name
                + "' : value {" + input + "} is not in scale domain: " + myNumberByDomainValue)
    }

    private fun fromNumber(v: Double?): Any? {
        if (v == null) {
            return null
        }

        if (myDomainValueByNumber == null) {
            myDomainValueByNumber = TreeMap()
            for (domainValue in myNumberByDomainValue.keys) {
                myDomainValueByNumber!![myNumberByDomainValue[domainValue]!!] = domainValue
            }
        }

        if (myDomainValueByNumber!!.containsKey(v)) {
            return myDomainValueByNumber!![v]
        }
        // look-up the closest key (number)
        val ceilingKey = myDomainValueByNumber!!.ceilingKey(v)
        val floorKey = myDomainValueByNumber!!.floorKey(v)
        var keyNumber: Double? = null
        if (ceilingKey != null || floorKey != null) {
            if (ceilingKey == null) {
                keyNumber = floorKey
            } else if (floorKey == null) {
                keyNumber = ceilingKey
            } else {
                val ceilingDist = Math.abs(ceilingKey - v)
                val floorDist = Math.abs(floorKey - v)
                keyNumber = if (ceilingDist < floorDist) ceilingKey else floorKey
            }
        }
        return if (keyNumber != null) myDomainValueByNumber!![keyNumber] else null
    }

    override fun with(): Scale2.Builder<T> {
        return MyBuilder(this)
    }

    private class MyBuilder<T> internal constructor(scale: DiscreteScale<T>) : AbstractScale.AbstractBuilder<Any, T>(scale) {
        internal val myIndexByValue: Map<Any, Double>
        private var myNewBreaks: List<Any>? = null
        internal var myDomainLimits: Set<Any> = emptySet()

        init {
            myIndexByValue = scale.myNumberByDomainValue
            myDomainLimits = scale.myDomainLimits
        }

        override fun lowerLimit(v: Double): Scale2.Builder<T> {
            throw IllegalArgumentException("Not applicable to scale with discrete domain")
        }

        override fun upperLimit(v: Double): Scale2.Builder<T> {
            throw IllegalArgumentException("Not applicable to scale with discrete domain")
        }

        override fun limits(domainValues: Set<Any>): Scale2.Builder<T> {
            myDomainLimits = domainValues
            return this
        }

        override fun breaks(l: List<*>): Scale2.Builder<T> {
            myNewBreaks = l as List<Any>
            // don't call super!
            return this
        }

        override fun continuousTransform(v: Transform): Scale2.Builder<T> {
            // ignore
            return this
        }

        override fun build(): Scale2<T> {
            val scale = DiscreteScale(this)
            if (myNewBreaks == null) {
                return scale
            }

            // breaks was updated
            scale.breaks = myNewBreaks!!

            // re-validate domain values
            val domainValues = myIndexByValue.keys
            if (!domainValues.containsAll(myNewBreaks!!)) {
                // extend domain to make sure all breaks are visible
                val newDomainValues = LinkedHashSet(myNewBreaks!!)
                newDomainValues.addAll(domainValues)
                scale.updateDomain(newDomainValues, scale.myDomainLimits)
            }
            return scale
        }
    }
}
