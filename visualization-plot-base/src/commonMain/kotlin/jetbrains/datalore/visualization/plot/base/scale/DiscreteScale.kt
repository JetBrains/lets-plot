package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.TreeMap
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import kotlin.math.abs

internal class DiscreteScale<T> : AbstractScale<Any, T> {
    private val myNumberByDomainValue = LinkedHashMap<Any, Double>()
    private var myDomainLimits: Set<Any?> = emptySet()
    private var myDomainValueByNumber: TreeMap<Double, Any>? = null

    override var breaks: List<Any?>
        get() {
            val breaks = super.breaks
            if (!hasDomainLimits()) {
                return breaks
            }
            val indices = SeriesUtil.matchingIndices(breaks, myDomainLimits)
            return SeriesUtil.pickAtIndices(breaks, indices)
        }
        set(breaks) {
            super.breaks = breaks
        }

    override val labels: List<String>
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

    override val domainLimits: ClosedRange<Double>
        get() = throw IllegalStateException("Not applicable to scale with discrete domain '$name'")

    constructor(name: String, domainValues: Collection<Any?>, mapper: ((Double?) -> T?)) : super(name, mapper) {
        updateDomain(domainValues, emptySet<Any>())

        // see: http://docs.ggplot2.org/current/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.6
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        updateDomain(b.myIndexByValue.keys, b.myDomainLimits)
    }

    private fun updateDomain(domainValues: Collection<Any?>, domainLimits: Set<Any?>) {
        val effectiveDomain: MutableList<Any?>
        if (domainLimits.isEmpty()) {
            effectiveDomain = ArrayList(domainValues)
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
                myDomainValueByNumber!!.put(myNumberByDomainValue[domainValue]!!, domainValue)
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
        return if (keyNumber != null) myDomainValueByNumber!![keyNumber] else null
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }

    private class MyBuilder<T> internal constructor(scale: DiscreteScale<T>) : AbstractScale.AbstractBuilder<Any, T>(scale) {
        internal val myIndexByValue: Map<Any, Double>
        private var myNewBreaks: List<Any?>? = null
        internal var myDomainLimits: Set<Any?> = emptySet()

        init {
            myIndexByValue = scale.myNumberByDomainValue
            myDomainLimits = scale.myDomainLimits
        }

        override fun lowerLimit(v: Double): Scale.Builder<T> {
            throw IllegalArgumentException("Not applicable to scale with discrete domain")
        }

        override fun upperLimit(v: Double): Scale.Builder<T> {
            throw IllegalArgumentException("Not applicable to scale with discrete domain")
        }

        override fun limits(domainValues: Set<*>): Scale.Builder<T> {
            myDomainLimits = domainValues
            return this
        }

        override fun breaks(l: List<*>): Scale.Builder<T> {
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
