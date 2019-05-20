package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.visualization.plot.base.render.Aes

object Scales {
    fun <T> continuousDomain(name: String, aes: Aes<T>): Scale2<T> {
        return ContinuousScale(name, Mappers.undefined(), aes.isNumeric)
    }

    fun continuousDomainNumericRange(name: String): Scale2<Double> {
        return ContinuousScale(name, Mappers.undefined(), true)
    }

    fun <T> continuousDomain(name: String, mapper: (Double?) -> T?, continuousRange: Boolean): Scale2<T> {
        return ContinuousScale(name, mapper, continuousRange)
    }

    /*
    fun <T> pureContinuous(name: String, mapper: (Double) -> T): Scale2<T> {
        return ContinuousScale(name, mapper, true)
    }
    */

    fun <T> discreteDomain(name: String, domainValues: Collection<Any?>): Scale2<T> {
        return discreteDomain(name, domainValues, Mappers.undefined())
    }

    fun <T> discreteDomain(name: String, domainValues: Collection<Any?>, mapper: ((Double?) -> T?)): Scale2<T> {
        return DiscreteScale(name, domainValues, mapper)
    }

    fun <T> pureDiscrete(name: String, domainValues: List<Any?>, outputValues: List<T>, defaultOutputValue: T): Scale2<T> {
        return Scales.discreteDomain<T>(name, domainValues)
                .with()
                .mapper(Mappers.discrete(outputValues, defaultOutputValue))
                .build()
    }
}
