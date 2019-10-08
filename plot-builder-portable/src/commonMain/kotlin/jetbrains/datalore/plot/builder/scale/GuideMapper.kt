package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.function.Function

interface GuideMapper<T> : Function<Double?, T?> {
    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean
}
