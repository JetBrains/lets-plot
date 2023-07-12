/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.jvm.JvmOverloads

class DataFrame private constructor(builder: Builder) {
    private val myVectorByVar: Map<Variable, List<*>>

    private val myIsNumeric: MutableMap<Variable, Boolean>
    private val myIsDateTime: MutableMap<Variable, Boolean>

    private val myOrderSpecs: List<OrderSpec>
    private val myFactorLevelsByVar: Map<Variable, List<Any>>

    private val myRanges = HashMap<Variable, DoubleSpan?>()
    private val myDistinctValues = HashMap<Variable, Set<Any>>()

    class OrderSpec(
        val variable: Variable,
        val orderBy: Variable,
        val direction: Int,
        val aggregateOperation: ((List<Double?>) -> Double?)? = null
    )

    val isEmpty: Boolean
        get() = myVectorByVar.isEmpty()

    init {
        assertAllSeriesAreSameSize(builder.myVectorByVar)
        myVectorByVar = HashMap(builder.myVectorByVar)
        myIsNumeric = HashMap(builder.myIsNumeric)
        myIsDateTime = HashMap(builder.myIsDateTime)
        myOrderSpecs = builder.myOrderSpecs
        myFactorLevelsByVar = builder.myFactorLevelsByVar
    }

    private fun assertAllSeriesAreSameSize(vectorByVar: Map<Variable, List<*>>) {
        if (vectorByVar.size > 1) {
            val entries = vectorByVar.entries.iterator()
            val first = entries.next()
            val size = first.value.size
            while (entries.hasNext()) {
                val next = entries.next()
                if (next.value.size != size) {
                    throw IllegalArgumentException(
                        "All data series in data frame must have equal size\n" + dumpSizes(
                            vectorByVar
                        )
                    )
                }
            }
        }
    }

    private fun dumpSizes(vectorByVar: Map<Variable, List<*>>): String {
        val sb = StringBuilder()
        for ((key, value) in vectorByVar) {
            sb.append(key.name)
                .append(" : ")
                .append(value.size)
                .append('\n')
        }
        return sb.toString()
    }

    fun rowCount(): Int {
        return if (myVectorByVar.isEmpty()) 0 else myVectorByVar.entries.iterator().next().value.size
    }

    fun has(variable: Variable): Boolean {
        return myVectorByVar.containsKey(variable)
    }

    fun isEmpty(variable: Variable): Boolean {
        return get(variable).isEmpty()
    }

    fun hasNoOrEmpty(variable: Variable): Boolean {
        return !has(variable) || isEmpty(variable)
    }

    operator fun get(variable: Variable): List<*> {
        assertDefined(variable)
        return myVectorByVar.getValue(variable)
    }

    fun getNumeric(variable: Variable): List<Double?> {
        assertDefined(variable)
        val list = myVectorByVar.getValue(variable)
        if (list.isNotEmpty()) {
            assertNumeric(variable)
        }

        @Suppress("UNCHECKED_CAST")
        return list as List<Double?>
    }

    fun distinctValues(variable: Variable): Collection<Any> {
        assertDefined(variable)
        return myDistinctValues.getOrPut(variable) {
            val orderSpec = myOrderSpecs.findLast { it.variable == variable }
            if (orderSpec != null) {
                getOrderedDistinctValues(orderSpec)
            } else if (myFactorLevelsByVar.containsKey(variable)) {
                myFactorLevelsByVar.getValue(variable).toSet().intersect(get(variable)).filterNotNull()
            } else {
                get(variable).filterNotNull().toSet()
            }.toSet()
        }
    }

    fun variables(): Set<Variable> {
        return myVectorByVar.keys
    }

    fun isNumeric(variable: Variable): Boolean {
        assertDefined(variable)
        return if (variable.isTransform) {
            true
        } else {
            myIsNumeric.getOrPut(variable) {
                val checkedDoubles = SeriesUtil.checkedDoubles(get(variable))
                checkedDoubles.notEmptyAndCanBeCast()
            }
        }
    }

    fun isDiscrete(variable: Variable): Boolean {
        return !isNumeric(variable)
    }

    fun isDateTime(variable: Variable): Boolean {
        assertDefined(variable)
        return myIsDateTime.containsKey(variable)
    }

    fun range(variable: Variable): DoubleSpan? {
        if (!myRanges.containsKey(variable)) {
            val v = getNumeric(variable)
            val r = SeriesUtil.range(v)
            myRanges[variable] = r
        }
        return myRanges[variable]
    }

    fun builder(): Builder {
        return Builder(this)
    }

    fun slice(indices: Iterable<Int>): DataFrame {
        return Builder(this, indices).build()
    }

    fun assertDefined(variable: Variable) {
        if (!has(variable)) {
            val e = IllegalArgumentException(undefinedVariableErrorMessage(variable.name))
            LOG.error(e) { e.message!! }
            throw e
        }
    }

    fun undefinedVariableErrorMessage(varName: String): String {
        return "Variable not found: '$varName'. Variables in data frame: ${
            this.variables().map { "'${it.name}'" }
        }"
    }

    private fun assertNumeric(variable: Variable) {
        if (!isNumeric(variable)) {
            val e = IllegalArgumentException("Not a numeric variable: '$variable'")
            LOG.error(e) { e.message!! }
            throw e
        }
    }

    fun selectIndices(indices: List<Int>): DataFrame {
        return buildModified { serie -> SeriesUtil.pickAtIndices(serie, indices) }
    }

    fun selectIndices(indices: Set<Int>): DataFrame {
        return buildModified { serie -> SeriesUtil.pickAtIndices(serie, indices) }
    }

    fun dropIndices(indices: Set<Int>): DataFrame {
        return if (indices.isEmpty()) this else buildModified { serie -> SeriesUtil.skipAtIndices(serie, indices) }
    }

    private fun buildModified(serieFun: (List<*>) -> List<*>): DataFrame {
        val builder = this.builder()
        for (variable in myVectorByVar.keys) {
            val serie = myVectorByVar[variable]
            val modifiedSerie = serieFun(serie!!)
            builder.putIntern(variable, modifiedSerie)
        }
        return builder.build()
    }

    private fun getOrderedDistinctValues(orderSpec: OrderSpec): Set<Any> {
        fun isValueComparable(value: Any?) = value != null && (value !is Double || value.isFinite())

        val orderedValues = if (orderSpec.aggregateOperation != null) {
            require(isNumeric(orderSpec.orderBy)) { "Can't apply aggregate operation to non-numeric values" }
            get(orderSpec.variable)
                .zip(getNumeric(orderSpec.orderBy))
                .groupBy({ (value) -> value }) { (_, byValue) -> byValue }
                .mapValues { (_, byValues) -> orderSpec.aggregateOperation.invoke(byValues.filter(::isValueComparable)) }
                .toList()
        } else {
            get(orderSpec.variable).zip(get(orderSpec.orderBy))
        }
            .filter { isValueComparable(it.second) && isValueComparable(it.first) }
            .sortedWith(compareBy({ it.second as Comparable<*> }, { it.first as Comparable<*> }))
            .mapNotNull { it.first }

        // the values corresponding to non-comparable values will be placed at the end of the result
        val nonComparableAppendix = get(orderSpec.variable).zip(get(orderSpec.orderBy))
            .filterNot { isValueComparable(it.second) }
            .mapNotNull { it.first }

        return (if (orderSpec.direction < 0) {
            orderedValues.reversed()
        } else {
            orderedValues
        } + nonComparableAppendix).toSet()
    }


    companion object {
        private val LOG = PortableLogging.logger(DataFrame::class)
    }


    class Variable @JvmOverloads constructor(
        val name: String,
        val source: Source = Source.ORIGIN,
        val label: String = name
    ) {

        val isOrigin: Boolean
            get() = source == Source.ORIGIN

        val isStat: Boolean
            get() = source == Source.STAT

        val isTransform: Boolean
            get() = source == Source.TRANSFORM

        override fun toString(): String {
            // important
            return name
        }

        fun toSummaryString(): String {
            return "$name, '$label' [$source]"
        }

        enum class Source {
            ORIGIN, TRANSFORM, STAT
        }

        companion object {

            @JvmOverloads
            fun createOriginal(name: String, label: String = name): Variable {
                return Variable(
                    name,
                    Source.ORIGIN,
                    label
                )
            }
        }
    }

    class Builder {
        internal val myVectorByVar = HashMap<Variable, List<*>>()
        internal val myIsNumeric = HashMap<Variable, Boolean>()
        internal val myIsDateTime = HashMap<Variable, Boolean>()
        internal val myOrderSpecs = ArrayList<OrderSpec>()
        internal val myFactorLevelsByVar = HashMap<Variable, List<Any>>()

        constructor()

        constructor(data: DataFrame) {
            initInternals(
                data.myVectorByVar,
                data.myIsNumeric,
                data.myIsDateTime,
                data.myOrderSpecs,
                data.myFactorLevelsByVar,
            )
        }

        internal constructor(data: DataFrame, indices: Iterable<Int>) {
            // Preserve "ordering"
            val addFactorLevelsByVar = data.variables()
                .filter { v -> !v.isTransform }
                .filter { v -> !(v in myFactorLevelsByVar) }
                .filter { v -> myOrderSpecs.find { it.variable == v } == null }
                .filter { v -> data.isDiscrete(v) }
                .associateWith { data.distinctValues(it).toList() }

            val newFactorLevelsByVar = data.myFactorLevelsByVar + addFactorLevelsByVar

            val newVectors = data.myVectorByVar.mapValues { (_, serie) ->
                serie.slice(indices)
            }

            initInternals(
                newVectors,
                data.myIsNumeric,
                data.myIsDateTime,
                data.myOrderSpecs,
                newFactorLevelsByVar,
            )
        }

        private fun initInternals(
            vectorByVar: Map<Variable, List<*>>,
            isNumeric: Map<Variable, Boolean>,
            isDateTime: Map<Variable, Boolean>,
            orderSpecs: List<OrderSpec>,
            factorLevelsByVar: Map<Variable, List<Any>>,
        ) {
            myVectorByVar.putAll(vectorByVar)
            myIsNumeric.putAll(isNumeric)
            myIsDateTime.putAll(isDateTime)
            myOrderSpecs.addAll(orderSpecs)
            myFactorLevelsByVar.putAll(factorLevelsByVar)
        }

        fun put(variable: Variable, v: List<*>): Builder {
            putIntern(variable, v)
            myIsNumeric.remove(variable)  // unknown state
            myIsDateTime.remove(variable)
            return this
        }

        fun putNumeric(variable: Variable, v: List<Double?>): Builder {
            putIntern(variable, v)
            myIsNumeric[variable] = true
            return this
        }

        fun putDiscrete(variable: Variable, v: List<*>): Builder {
            putIntern(variable, v)
            myIsNumeric[variable] = false
            return this
        }

        fun putDateTime(variable: Variable, v: List<*>): Builder {
            putIntern(variable, v)
            myIsDateTime[variable] = true
            return this
        }

        internal fun putIntern(variable: Variable, v: List<*>) {
            myVectorByVar[variable] = ArrayList(v)
        }

        fun remove(variable: Variable): Builder {
            myVectorByVar.remove(variable)
            myIsNumeric.remove(variable)
            myIsDateTime.remove(variable)
            return this
        }

        fun addOrderSpecs(orderSpecs: List<OrderSpec>): Builder {
            orderSpecs.forEach(::addOrderSpec)
            return this
        }

        fun addOrderSpec(orderSpec: OrderSpec): Builder {
            val currentOrderSpec = myOrderSpecs.find { it.variable == orderSpec.variable }
            // If multiple specifications for the variable - choose a more specific one:
            if (currentOrderSpec?.aggregateOperation == null) {
                myOrderSpecs.remove(currentOrderSpec)
                myOrderSpecs.add(orderSpec)
            }
            return this
        }

        fun addFactorLevels(factorLevelsByVar: Map<Variable, List<Any>>): Builder {
            myFactorLevelsByVar.putAll(factorLevelsByVar)
            return this
        }

        fun build(): DataFrame {
            return DataFrame(this)
        }

        companion object {
            fun emptyFrame(): DataFrame {
                return Builder().build()
            }
        }
    }
}