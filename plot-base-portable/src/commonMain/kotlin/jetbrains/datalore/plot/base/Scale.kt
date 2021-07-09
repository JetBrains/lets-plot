/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.scale.BreaksGenerator

/**
 * Translates input to aesthetics by
 * 1) Transforming data (like x1=log(x)). Must happen before 'stat' is applies to data
 * 2) Mapping data to aesthetic
 *
 *
 * name - (axis/legend title)
 * brakes (domain values) - ticks on axis, items/segments on legends
 * labels - tick labels
 *
 * @param <T> - type of target aesthetic
 *
 */
interface Scale<T> {
    val name: String

    val breaks: List<Any>

    val labels: List<String>

    val labelFormatter: ((Any) -> String)?

    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean

    val isContinuousDomain: Boolean

    val domainLimits: Pair<Double, Double>

    val multiplicativeExpand: Double

    val additiveExpand: Double

    val transform: Transform

    val mapper: (Double?) -> T?

    val breaksGenerator: BreaksGenerator

    fun hasBreaks(): Boolean

    fun hasLabels(): Boolean

    fun hasDomainLimits(): Boolean

    fun isInDomainLimits(v: Any): Boolean

    fun asNumber(input: Any?): Double?

    fun hasBreaksGenerator(): Boolean

    fun with(): Builder<T>

    interface Builder<T> {

        /**
         * Lower limit for scale with continuous domain.
         */
        fun lowerLimit(v: Double): Builder<T>

        /**
         * Upper limit for scale with continuous domain.
         */
        fun upperLimit(v: Double): Builder<T>

        /**
         * Limits for scale with discrete domain
         */
        fun limits(domainValues: List<Any>): Builder<T>

        fun breaks(l: List<Any>): Builder<T>

        fun labels(l: List<String>): Builder<T>

        fun labelFormatter(v: (Any) -> String): Builder<T>

        fun mapper(m: (Double?) -> T?): Builder<T>

        fun multiplicativeExpand(v: Double): Builder<T>

        fun additiveExpand(v: Double): Builder<T>

        fun continuousTransform(v: Transform): Builder<T>

        fun breaksGenerator(v: BreaksGenerator): Builder<T>

        fun build(): Scale<T>
    }
}
