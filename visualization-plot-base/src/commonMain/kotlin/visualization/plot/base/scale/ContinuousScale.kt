package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.scale.transform.Transforms

internal class ContinuousScale<T> : AbstractScale<Double, T> {
    override val isContinuous: Boolean
    override val domainLimits: ClosedRange<Double>

    override val isContinuousDomain: Boolean = true

    override val defaultTransform: Transform
        get() = Transforms.IDENTITY

    constructor(name: String, mapper: ((Double?) -> T?), continuousOutput: Boolean) : super(name, mapper) {
        isContinuous = continuousOutput
        domainLimits = ClosedRange.closed(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

        // see: http://docs.ggplot2.org/current/scale_continuous.html
        // defaults for continuous scale.
        multiplicativeExpand = 0.05
        additiveExpand = 0.0
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        isContinuous = b.myContinuousOutput
        domainLimits = ClosedRange.closed(b.myLowerLimit, b.myUpperLimit)
    }

    override fun isInDomainLimits(v: Any): Boolean {
        return v is Number && domainLimits.contains(v.toDouble())
    }

    override fun hasDomainLimits(): Boolean {
        return domainLimits.lowerEndpoint() > Double.NEGATIVE_INFINITY || domainLimits.upperEndpoint() < Double.POSITIVE_INFINITY
    }

    override fun asNumber(input: Any?): Double? {
        if (input == null || input is Double) {
            return input as Double?
        }
        throw IllegalArgumentException("Double is expected but was " + input::class.simpleName + " : " + input.toString())
    }

    override fun with(): Scale2.Builder<T> {
        return MyBuilder(this)
    }


    private class MyBuilder<T>(scale: ContinuousScale<T>) : AbstractBuilder<Double, T>(scale) {
        internal val myContinuousOutput: Boolean = scale.isContinuous
        internal var myLowerLimit: Double = 0.0
        internal var myUpperLimit: Double = 0.0

        init {
            myLowerLimit = scale.domainLimits.lowerEndpoint()
            myUpperLimit = scale.domainLimits.upperEndpoint()
        }

        override fun lowerLimit(v: Double): Scale2.Builder<T> {
            myLowerLimit = v
            return this
        }

        override fun upperLimit(v: Double): Scale2.Builder<T> {
            myUpperLimit = v
            return this
        }

        override fun limits(domainValues: Set<*>): Scale2.Builder<T> {
            throw IllegalArgumentException("Can't apply discrete limits to scale with continuous domain")
        }

        override fun continuousTransform(v: Transform): Scale2.Builder<T> {
            return transform(v)
        }

        override fun build(): Scale2<T> {
            return ContinuousScale(this)
        }
    }
}
