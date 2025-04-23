/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

final class DiscreteTransform constructor(
    domainValues: Collection<Any>,
    private val domainLimits: List<Any>
) : Transform {

    private val indexByDomainValue: Map<Any, Double>
    private val domainValueByIndex: Map<Double, Any>

    val initialDomain: List<Any> = domainValues.distinct()
    val effectiveDomain: List<Any>
    val effectiveDomainTransformed: List<Double>

    init {
        effectiveDomain = if (domainLimits.isEmpty()) {
            initialDomain
        } else {
            domainLimits.distinct()
        }

        indexByDomainValue = effectiveDomain.mapIndexed { index, value -> value to index.toDouble() }.toMap()
        domainValueByIndex = indexByDomainValue.map { it.value to it.key }.toMap()
        effectiveDomainTransformed = effectiveDomain.map { indexByDomainValue.getValue(it) }
    }

    override fun hasDomainLimits(): Boolean {
        return domainLimits.isNotEmpty()
    }

    override fun isInDomain(v: Any?): Boolean {
        return indexByDomainValue.containsKey(v) ||
                v is Number  // Number can always be 'tramsformed'.
    }

    fun indexOf(v: Any): Int {
        return indexByDomainValue[v]?.toInt() ?: -1
    }

    override fun apply(l: List<*>): List<Double?> {
        return l.map { transformToNumber(it) }
    }

    override fun applyInverse(v: Double?): Any? {
        return inverseFromNumber(v)
    }

    private fun transformToNumber(input: Any?): Double? {
        if (input == null) {
            return null
        }

        return if (input in indexByDomainValue) {
            // If the input exists within the disctere domain.
            indexByDomainValue.getValue(input)
        } else if (input is Number) {
            // Just 'transparent' for numbers.
            input.toDouble()
        } else throw IllegalStateException(
            "value $input is not in the domain: ${effectiveDomain}"
        )
    }

    private fun inverseFromNumber(v: Double?): Any? {
        if (v == null || !v.isFinite()) {
            return null
        }

        return if (v in domainValueByIndex) {
            domainValueByIndex.getValue(v)
        } else {
            null
        }

//        val i = v.roundToInt()
//        return if (i >= 0 && i < effectiveDomain.size) {
//            effectiveDomain[i]
//        } else {
//            null
//        }
    }

    fun withMoreLimits(limits: Collection<Any>): DiscreteTransform {
        val expandedLimits = domainLimits.union(limits).toList()
        return DiscreteTransform(initialDomain, expandedLimits)
    }

    fun withDomain(domainValues: Collection<Any>): DiscreteTransform {
        return DiscreteTransform(domainValues, domainLimits)
    }

    companion object {
        fun join(l: List<DiscreteTransform>): DiscreteTransform {
            val domainValues = LinkedHashSet<Any>()
            val domainLimits = LinkedHashSet<Any>()
            for (transform in l) {
                domainValues.addAll(transform.initialDomain)
                domainLimits.addAll(transform.domainLimits)
            }
            return DiscreteTransform(domainValues, domainLimits.toList())
        }
    }
}