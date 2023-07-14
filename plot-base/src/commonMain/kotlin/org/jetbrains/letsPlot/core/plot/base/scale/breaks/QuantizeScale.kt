/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.math.max
import kotlin.math.min


// ToDo: seem like `WithFiniteOrderedOutput` never used
class QuantizeScale<T> : WithFiniteOrderedOutput<T> {
    private var myHasDomain: Boolean = false
    private var myDomainStart: Double = 0.0
    private var myDomainEnd: Double = 0.0
    private lateinit var myOutputValues: List<T>

    override val outputValues: List<T>
        get() = myOutputValues

    //return Arrays.asList(ClosedRangeOpen(myDomainStart, myDomainEnd));
    //    double error = bucketSize / 10;   // prevent creating of 1 extra bucket
    /*
    double upperBound = myDomainStart;
    while (upperBound < myDomainEnd - error) {
      double lowerBound = upperBound;
      upperBound = lowerBound + bucketSize;
      DoubleSpan bucket = ClosedRangeOpen(lowerBound, upperBound);
      list.add(bucket);
    }
*///DoubleSpan bucket = ClosedRangeOpen(myDomainStart + bucketSize * i, myDomainStart + bucketSize * (i + 1));
    // ToDo: move inside the cycle
    // last bucket - closed
    val domainQuantized: List<DoubleSpan>
        get() {
            if (myDomainStart == myDomainEnd) {
                return listOf(DoubleSpan(myDomainStart, myDomainEnd))
            }

            val list = ArrayList<DoubleSpan>()
            val numBuckets = myOutputValues.size
            val bucketSize = bucketSize()
            for (i in 0 until numBuckets - 1) {
                val bucket = DoubleSpan(myDomainStart + bucketSize * i, myDomainStart + bucketSize * (i + 1))
                list.add(bucket)
            }
            val bucket = DoubleSpan(myDomainStart + bucketSize * (numBuckets - 1), myDomainEnd)
            list.add(bucket)
            return list
        }

    /**
     * Set the scale's input domain.
     */
    fun domain(start: Double, end: Double): QuantizeScale<T> {
        require(start <= end) { "Domain start must be less then domain end: $start > $end" }
        myHasDomain = true
        myDomainStart = start
        myDomainEnd = end
        return this
    }

    /**
     * Scale's output 'quantized' values
     */
    fun range(values: Collection<T>): QuantizeScale<T> {
        myOutputValues = ArrayList(values)
        return this
    }

    fun quantize(v: Double): T {
        val i = outputIndex(v)
        return myOutputValues[i]
    }

    private fun outputIndex(v: Double): Int {
        check(myHasDomain) { "Domain not defined." }
        check(::myOutputValues.isInitialized && myOutputValues.isNotEmpty()) { "Output values are not defined." }
        val bucketSize = bucketSize()
        val index = ((v - myDomainStart) / bucketSize).toInt()
        val maxIndex = myOutputValues.size - 1
        return max(0, min(maxIndex, index))
    }

    override fun getOutputValueIndex(domainValue: Any): Int {
        return if (domainValue is Number) {
            outputIndex(domainValue.toDouble())
        } else -1
    }

    override fun getOutputValue(domainValue: Any): T? {
        return if (domainValue is Number) {
            quantize(domainValue.toDouble())
        } else null
    }

    private fun bucketSize(): Double {
        return (myDomainEnd - myDomainStart) / myOutputValues.size
    }
}
