package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange

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
</T> */
interface Scale2<T> {
    val name: String

    val breaks: List<*>

    val labels: List<String>

    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean

    val isContinuousDomain: Boolean

    val domainLimits: ClosedRange<Double>

    val multiplicativeExpand: Double

    val additiveExpand: Double

    val transform: Transform

    val mapper: ((Double?) -> T)

    val breaksGenerator: BreaksGenerator
        get() {
            val transform = transform
            if (transform is BreaksGenerator) {
                return transform as BreaksGenerator
            }
            throw IllegalStateException("No breaks generator for '$name'")
        }

    fun hasBreaks(): Boolean

    fun hasLabels(): Boolean

    fun hasDomainLimits(): Boolean

    fun isInDomainLimits(v: Any): Boolean

    fun asNumber(input: Any?): Double?

    fun hasBreaksGenerator(): Boolean {
        return transform is BreaksGenerator
    }

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
        fun limits(domainValues: Set<*>): Builder<T>

        fun breaks(l: List<*>): Builder<T>

        fun labels(l: List<String>): Builder<T>

        fun mapper(m: (Double?) -> T): Builder<T>

        fun multiplicativeExpand(v: Double): Builder<T>

        fun additiveExpand(v: Double): Builder<T>

        fun continuousTransform(v: Transform): Builder<T>

        fun build(): Scale2<T>
    }
}
