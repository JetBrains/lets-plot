package jetbrains.datalore.visualization.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.builder.scale.DefaultNaValue

class AlphaMapperProvider(
        range: ClosedRange<Double>,
        naValue: Double) :
        LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = ClosedRange.closed(0.1, 1.0)

        val DEFAULT = AlphaMapperProvider(DEF_RANGE, DefaultNaValue[ALPHA])
    }
}