package jetbrains.datalore.plot.base

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface Aesthetics {
    val isEmpty: Boolean

    fun dataPointAt(index: Int): DataPointAesthetics

    fun dataPointCount(): Int

    fun dataPoints(): Iterable<DataPointAesthetics>

    /**
     * Numeric aes only (x,y)
     *
     * @return The range of mapped data
     */
    fun range(aes: Aes<Double>): ClosedRange<Double>?

    /**
     * Numeric aes only (x,y)
     *
     * @return The length of the entire axis
     */
    fun overallRange(aes: Aes<Double>): ClosedRange<Double>

    fun resolution(aes: Aes<Double>, naValue: Double): Double

    fun numericValues(aes: Aes<Double>): Iterable<Double>

    fun groups(): Iterable<Int>
}
