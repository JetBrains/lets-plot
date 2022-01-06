/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import kotlin.math.roundToInt

final class DiscreteTransform(
    val domainValues: Collection<Any>,  // ToDo: (?) should not be exposed.
    val domainLimits: List<Any>   // ToDo: (?) only 'effectiveDomain' makes sence.
) : Transform {

    private val indexByDomainValue: Map<Any, Int>

    val effectiveDomain: List<Any>
    val effectiveDomainTransformed: List<Double>

    init {
        effectiveDomain = if (domainLimits.isEmpty()) {
            domainValues.distinct()
        } else {
            domainLimits.intersect(domainValues).toList().distinct()
        }

        indexByDomainValue = effectiveDomain.mapIndexed { index, value -> value to index }.toMap()
        effectiveDomainTransformed = effectiveDomain.map { indexByDomainValue.getValue(it).toDouble() }
    }

    fun hasDomainLimits(): Boolean {
        return domainLimits.isNotEmpty()
    }

    fun isInDomain(v: Any?): Boolean {
        return indexByDomainValue.containsKey(v)
    }

    fun indexOf(v: Any): Int {
        return indexByDomainValue.getValue(v)
    }

    override fun apply(l: List<*>): List<Double?> {
        return l.map { asNumber(it) }
    }

    override fun applyInverse(v: Double?): Any? {
        return fromNumber(v)
    }

    private fun asNumber(input: Any?): Double? {
        if (input == null) {
            return null
        }
        if (indexByDomainValue.containsKey(input)) {
            return indexByDomainValue.getValue(input).toDouble()
        }

        throw IllegalStateException(
            "value $input is not in the domain: ${effectiveDomain}"
        )
    }

    private fun fromNumber(v: Double?): Any? {
        if (v == null || !v.isFinite()) {
            return null
        }

        val i = v.roundToInt()
        return if (i >= 0 && i < effectiveDomain.size) {
            effectiveDomain[i]
        } else {
            null
        }
    }
}