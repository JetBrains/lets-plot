package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

object Scales {
    fun <T> continuousDomain(name: String, aes: Aes<T>): Scale2<T> {
        return ContinuousScale(name, null, aes.isNumeric)
    }

    fun continuousDomainNumericRange(name: String): Scale2<Double> {
        return ContinuousScale(name, null, true)
    }

    fun <T> continuousDomain(name: String, mapper: (Double) -> T, continuousRange: Boolean): Scale2<T> {
        return ContinuousScale(name, mapper, continuousRange)
    }

    /*
  public static <T> Scale2<T> pureContinuous(String name, Function<Double, T> mapper) {
    return new ContinuousScale<>(name, mapper, true);
  }
  */

    fun <T> discreteDomain(name: String, domainValues: Collection<Any>): Scale2<T> {
        return discreteDomain(name, domainValues, null)
    }

    fun <T> discreteDomain(name: String, domainValues: Collection<Any>, mapper: ((Double) -> T)?): Scale2<T> {
        return DiscreteScale(name, domainValues, mapper)
    }

    fun <T> pureDiscrete(name: String, domainValues: List<Any>, outputValues: List<T>, defaultOutputValue: T): Scale2<T> {
        return Scales.discreteDomain<T>(name, domainValues)
                .with()
                .mapper(Mappers.discrete(outputValues, defaultOutputValue))
                .build()
    }
}
