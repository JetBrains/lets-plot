package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.render.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.gog.plot.scale.DefaultNaValue

internal class AlphaMapperProvider(
        range: ClosedRange<Double>,
        naValue: Double) :
        LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = ClosedRange.closed(0.1, 1.0)

        val DEFAULT = AlphaMapperProvider(DEF_RANGE, DefaultNaValue.get(ALPHA))
    }
}