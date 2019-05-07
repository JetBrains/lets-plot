package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.base.function.Function

interface GuideMapper<T> : Function<Double?, T?> {
    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean
}
