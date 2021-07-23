/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.gcommon.collect.TreeMap
import jetbrains.datalore.plot.base.scale.MapperUtil
import kotlin.math.abs

final class DiscreteTransform(
    val domainValues: Collection<Any>,
    val domainLimits: List<Any>
) : Transform {

    private val numberByDomainValue = LinkedHashMap<Any, Double>()
    private val domainValueByNumber: TreeMap<Double, Any> = TreeMap()

    init {
        val effectiveDomain = if (domainLimits.isEmpty()) {
            domainValues
        } else {
            domainLimits.intersect(domainValues)
        }

        numberByDomainValue.putAll(
            MapperUtil.mapDiscreteDomainValuesToNumbers(effectiveDomain)
        )

        for ((domainValue, number) in numberByDomainValue) {
            domainValueByNumber.put(number, domainValue)
        }
    }

    fun hasDomainLimits(): Boolean {
        return domainLimits.isNotEmpty()
    }

    fun isInDomain(v: Any?): Boolean {
        return numberByDomainValue.containsKey(v)
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
        if (numberByDomainValue.containsKey(input)) {
            return numberByDomainValue[input]
        }

        throw IllegalArgumentException(
            "value $input is not in the domain: ${numberByDomainValue.keys}"
        )
    }

    private fun fromNumber(v: Double?): Any? {
        if (v == null) {
            return null
        }

        if (domainValueByNumber.containsKey(v)) {
            return domainValueByNumber[v]
        }

        // look-up the closest key (number)
        val ceilingKey = domainValueByNumber.ceilingKey(v)
        val floorKey = domainValueByNumber.floorKey(v)
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
        return if (keyNumber != null) domainValueByNumber[keyNumber] else null
    }
}